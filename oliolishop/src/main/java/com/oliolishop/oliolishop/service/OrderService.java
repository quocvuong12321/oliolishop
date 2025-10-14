package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.order.OrderItemRequest;
import com.oliolishop.oliolishop.dto.order.OrderItemResponse;
import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.enums.TransactionStatus;
import com.oliolishop.oliolishop.enums.TransactionType;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.OrderItemMapper;
import com.oliolishop.oliolishop.mapper.OrderMapper;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.util.AppUtils;
import com.oliolishop.oliolishop.util.ProductSkuUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.weaver.ast.Or;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderItemRepository orderItemRepository;
    OrderRepository orderRepository;
    OrderItemMapper orderItemMapper;
    CustomerRepository customerRepository;
    AddressRepository addressRepository;
    VoucherRepository voucherRepository;
    OrderMapper orderMapper;
    ProductSkuRepository productSkuRepository;
    ProductSpuRepository productSpuRepository;
    ProductSkuUtils productSkuUtils;
    VNPayService vnPayService;
   TransactionRepository transactionRepository;
   PaymentMethodRepository paymentMethodRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        String customerId = AppUtils.getCustomerIdByJwt();
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        Address address = addressRepository.findById(request.getAddressId()).orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        Voucher voucher = null;
        if (!request.getVoucherCode().isEmpty()) {
            voucher = voucherRepository.findByVoucherCode(request.getVoucherCode()).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));
        }

        Order order = orderMapper.toOrder(request);
        order.setId(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setAddress(address);
        order.setVoucher(voucher);
        order.setFeeShip(request.getFeeShip());
        order.setOrderStatus(OrderStatus.pending_payment);
        order.setShippingAddress(request.getShippingAddress());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest item : request.getOrderItems()) {
            ProductSku sku = productSkuRepository.findById(item.getProductSkuId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

            if (sku.getSkuStock() < item.getQuantity())
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);

            sku.setSkuStock(sku.getSkuStock() - item.getQuantity());

            OrderItem orderItem = orderItemMapper.toOrderItem(item);

            orderItem.setOrder(order);

            orderItem.setUnitPrice(sku.getOriginalPrice());

            orderItem.setId(UUID.randomUUID().toString());

            orderItem.setReturnQuantity(0);

            orderItem.setAllowReturn(true);

            orderItem.setProductSku(sku);

            totalAmount = totalAmount.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if (voucher != null &&
                totalAmount.compareTo(BigDecimal.valueOf(voucher.getMinOrderValue())) > 0) {
            order.setVoucherCode(voucher.getVoucherCode());

            BigDecimal percentDiscount = BigDecimal
                    .valueOf(voucher.getDiscountPercent())
                    .multiply(totalAmount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal maxDiscount = BigDecimal.valueOf(voucher.getMaxDiscountValue());

            voucherDiscount = percentDiscount.min(maxDiscount);

            voucher.setAmount(voucher.getAmount() - 1);
        }

        order.setVoucherDiscountAmount(voucherDiscount);

        BigDecimal finalAmount = totalAmount
                .add(request.getFeeShip())
                .subtract(voucherDiscount)
                .setScale(2, RoundingMode.HALF_UP);

        order.setFinalAmount(finalAmount);

        BigDecimal loyalPoint = finalAmount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if(customer.getLoyaltyPoints() != null)
            customer.setLoyaltyPoints(customer.getLoyaltyPoints().add(loyalPoint));
        else
            customer.setLoyaltyPoints(loyalPoint);


        OrderResponse response = orderMapper.toResponse(orderRepository.save(order));
        response.setId(order.getId());
        response.setStatus(order.getOrderStatus());
        response.setCreateDate(order.getCreateDate());


        List<OrderItemResponse> orderItemResponses = new ArrayList<>();

        orderItems.forEach(orderItem -> {
            OrderItemResponse itemResponse = orderItemMapper.toResponse(orderItem);
            ProductSku sku = orderItem.getProductSku();
            ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());
            itemResponse.setId(orderItem.getId());
            itemResponse.setProductSkuId(sku.getId());
            itemResponse.setName(spu.getName());
            itemResponse.setThumbnail(spu.getImage());
            itemResponse.setVariant(productSkuUtils.getVariant(sku));
            orderItemResponses.add(itemResponse);
        });

        response.setOrderItems(orderItemResponses);

        return response;
    }

    public String createVnPayPayment(String orderId,String paymentMethodId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if(!order.getOrderStatus().equals(OrderStatus.pending_payment))
            throw new AppException(ErrorCode.ORDER_PAID);

        int amount = order.getFinalAmount().intValue();

        String orderInfo = "THANH TOAN DON HANG #" + orderId;

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .order(order)
                .amount(order.getFinalAmount())
                .transactionType(TransactionType.payment)
                .status(TransactionStatus.pending)
                .paymentMethod(PaymentMethod.builder()
                        .id(paymentMethodId).build())
                .build();

        transactionRepository.save(transaction);

        String returnUrl = ApiPath.FULLURL
                + ApiPath.Payment.ROOT
                + ApiPath.Payment.VNPAY_RETURN
                + "?transactionId=" + transaction.getId();

        return vnPayService.createOrder(amount, orderInfo, returnUrl);
    }

    public int updateStatusTransaction(HttpServletRequest request) {

        int result = vnPayService.orderReturn(request);

        String transactionId = request.getParameter("transactionId");

        Transaction existedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new AppException(ErrorCode.TRANSACTION_NOT_EXIST));
        String gatewayTransactionId = request.getParameter("vnp_TransactionNo");
        existedTransaction.setGatewayTransactionId(gatewayTransactionId);

        Order order = existedTransaction.getOrder();

        switch (result) {
            case 1: {
                existedTransaction.setStatus(TransactionStatus.success);
                order.setOrderStatus(OrderStatus.pending_confirmation);
                break;
            }
            case 0:{
                existedTransaction.setStatus(TransactionStatus.failed);
                order.setOrderStatus(OrderStatus.payment_failed);
                break;
            }
            default: throw new AppException(ErrorCode.PAYMENT_INVALID);
        }
        transactionRepository.save(existedTransaction);
        orderRepository.save(order);
        return result;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    public void checkPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.pending);
        LocalDateTime now = LocalDateTime.now();
        for(Transaction tx : pendingTransactions){
            // Nếu quá hạn thanh toán (ví dụ hơn 15 phút từ createDate)
            if(tx.getCreateDate().plusMinutes(15).isBefore(now)){
                tx.setStatus(TransactionStatus.failed);
                Order order = tx.getOrder();
                order.setOrderStatus(OrderStatus.payment_failed);
                transactionRepository.save(tx);
                orderRepository.save(order);
            }
        }
    }

    public void confirmOrder(String orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()->new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if(!order.getOrderStatus().equals(OrderStatus.pending_confirmation))
            throw new AppException(ErrorCode.ORDER_STATUS_INVALID);

        String employeeId = AppUtils.getEmployeeIdByJwt();
        if (employeeId == null || employeeId.isEmpty())
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        order.setOrderStatus(OrderStatus.confirmed);
        order.setConfirmBy(Employee.builder().id(employeeId).build());
        order.setConfirmDate(LocalDateTime.now());
        orderRepository.save(order);
    }

}
