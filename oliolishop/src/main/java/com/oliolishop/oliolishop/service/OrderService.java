package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.cart.CartItemRequest;
import com.oliolishop.oliolishop.dto.ghn.GhnPreviewRequest;
import com.oliolishop.oliolishop.dto.ghn.GhnPreviewResponse;
import com.oliolishop.oliolishop.dto.order.*;
import com.oliolishop.oliolishop.dto.payment.PaymentMethodResponse;
import com.oliolishop.oliolishop.dto.voucher.VoucherResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.enums.TransactionStatus;
import com.oliolishop.oliolishop.enums.TransactionType;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.*;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.util.AppUtils;
import com.oliolishop.oliolishop.util.OrderUtils;
import com.oliolishop.oliolishop.util.ProductSkuUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {
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
    AddressMapper addressMapper;
    PaymentMethodMapper paymentMethodMapper;
    VoucherMapper voucherMapper;
    LocationMapper locationMapper;
    GhnService ghnService;
    RatingRepository ratingRepository;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    Random random = new Random();

    @Transactional
    public CheckOutResponse checkOut(CheckOutRequest request) {

        String customerId = AppUtils.getCustomerIdByJwt();

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
        Map<String, Integer> skuQuantityMap = request.getCartItemRequests().stream()
                .collect(Collectors.toMap(
                        CartItemRequest::getProductSkuId, // Key: productSkuId
                        CartItemRequest::getQuantity      // Value: quantity
                        // Thêm (oldValue, newValue) -> oldValue nếu cần xử lý SKU trùng lặp
                ));

        List<String> skuIds = skuQuantityMap.keySet().stream().toList();
        List<ProductSku> skuList = productSkuRepository.findAllByIdIn(skuIds);


        if (skuList.size() != skuIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXIST);
        }

        List<CheckOutItemResponse> checkOutItemResponses = skuList.stream().map(s -> {
            if (s.getSkuStock() < skuQuantityMap.get(s.getId()))
                throw new AppException(ErrorCode.NOT_ENOUGH_QUANTITY_PRODUCT);
            ProductSpu spu = s.getSpu();
            return CheckOutItemResponse.builder()
                    .name(spu.getName())
                    .price(s.getOriginalPrice())
                    .productSkuId(s.getId())
                    .productSpuId(spu.getId())
                    .quantity(skuQuantityMap.get(s.getId()))
                    .thumbnail(spu.getImage())
                    .variant(productSkuUtils.getVariant(s))
                    .weight(s.getWeight())
                    .build();
        }).toList();

        List<AddressResponse> addressResponses = addressRepository.findByCustomerIdWithDetail(customerId).orElse(new ArrayList<>()).stream().map(address -> {
            AddressResponse addressResponse = addressMapper.toResponse(address);
            addressResponse.setWard(locationMapper.toWardDTO(address.getWard()));
            addressResponse.setDistrict(locationMapper.toDistrictDTO(address.getWard().getDistrict()));
            addressResponse.setProvince(locationMapper.toProvinceDTO(address.getWard().getDistrict().getProvince()));
            addressResponse.setDefaultAddress(address.isDefaultAddress());
            return addressResponse;
        }).toList();

        List<PaymentMethodResponse> paymentMethodResponses = paymentMethodRepository.findAll().stream().map(paymentMethodMapper::toResponse).toList();

        CheckOutResponse response = CheckOutResponse.builder()
                .checkOutItemResponses(checkOutItemResponses)
                .address(addressResponses)
                .paymentMethod(paymentMethodResponses)
                .build();

        List<Voucher> vouchers = voucherRepository.findByTotalPrice(response.getTotalAmount(), customerId).orElse(new ArrayList<>());

        List<VoucherResponse> voucherResponses = vouchers
                .stream().map(voucherMapper::response).toList();
        response.setVouchers(voucherResponses);

        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = response.getTotalAmount();
        if (request.getVoucherCode() != null) {
            VoucherResponse voucherApplied = voucherResponses.stream().filter(voucherResponse -> voucherResponse.getVoucherCode().equals(request.getVoucherCode())).findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));

            if (voucherApplied.getAmount() <= 0 || voucherApplied.getStatus().equals(VoucherStatus.Inactive)) {
                Voucher expiredVoucher = (Voucher) vouchers.stream().filter(v -> v.getId().equals(voucherApplied.getId()));
                expiredVoucher.setStatus(VoucherStatus.Inactive);
                voucherRepository.save(expiredVoucher);
                throw new AppException(ErrorCode.NOT_ENOUGH_QUANTITY_VOUCHER);
            }

            discountAmount = response.getTotalAmount().multiply(BigDecimal.valueOf(voucherApplied.getDiscountPercent())).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

            discountAmount = discountAmount.min(BigDecimal.valueOf(voucherApplied.getMaxDiscountValue()));

            finalAmount = response.getTotalAmount().subtract(discountAmount);
            response.setAppliedVoucherCode(voucherApplied.getVoucherCode());
        }


        response.setDiscountAmount(discountAmount);

        List<GhnPreviewRequest.GhnItem> ghnItems = checkOutItemResponses.stream().map(sku -> GhnPreviewRequest.GhnItem.builder()
                .weight(sku.getWeight().intValue())
                .name(sku.getName())
                .quantity(sku.getQuantity())
                .build()).toList();

        AddressResponse addressDefault = addressResponses.stream().filter(AddressResponse::isDefaultAddress).findFirst().orElse(null);
        if (request.getAddressId() != null) {
            addressDefault = addressResponses.stream()
                    .filter(addressResponse -> addressResponse.getId().equals(request.getAddressId())).findFirst().orElse(null);
        }

        if (addressDefault == null)
            return response;

        GhnPreviewRequest ghnPreviewRequest = GhnPreviewRequest.builder()
                .items(ghnItems)
                .to_address(addressDefault.getDetailAddress())
                .to_name(addressDefault.getName())
                .to_phone(addressDefault.getPhoneNumber())
                .to_ward_code(addressDefault.getWard().getId())
                .weight((int) Math.round(response.getTotalWeight() * 100))
                .build();

        GhnPreviewResponse ghnPreviewResponse = ghnService.getPreview(ghnPreviewRequest);

        BigDecimal feeShip = ghnPreviewResponse.getData().getTotal_fee();

        response.setFeeShip(feeShip);
        response.setExpectedDeliveryTime(AppUtils.pasteStringToDateTime(ghnPreviewResponse.getData().getExpected_delivery_time()));
        response.setFinalAmount(finalAmount.add(feeShip));
        response.setLoyalPoint(customer.getLoyaltyPoints());

        return response;
    }

    public OrderResponse getOrderById(String orderId) {

        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        OrderResponse response = orderMapper.toResponse(order);

        response.setStatus(order.getOrderStatus());

        response.setOrderItems(order.getOrderItems().stream().map(item -> {
            OrderItemResponse itemResponse = orderItemMapper.toResponse(item);
            ProductSku sku = item.getProductSku();
            ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());
            boolean rated = ratingRepository.existsByOrderItem_Id(item.getId());
            itemResponse.setName(spu.getName());
            itemResponse.setProductSkuId(sku.getId());
            itemResponse.setThumbnail(spu.getImage());
            itemResponse.setProductSpuId(spu.getId());
            itemResponse.setVariant(productSkuUtils.getVariant(sku));
            itemResponse.setRated(rated);
            return itemResponse;
        }).collect(Collectors.toList()));

        return response;

    }


    public PaginatedResponse<OrderResponse> getOrdersByCustomerId(List<OrderStatus> statuses, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        String customerId = AppUtils.getCustomerIdByJwt();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> ordersPage;

        if (statuses == null || statuses.isEmpty()) {
            ordersPage = orderRepository.findByCustomerId(customerId, pageable);
        } else {
            ordersPage = orderRepository.findByCustomerIdAndOrderStatusIn(customerId, statuses, pageable);
        }

        Page<OrderResponse> responsePage = ordersPage.map(item -> {
            OrderResponse orderResponse = orderMapper.toResponse(item);
            orderResponse.setStatus(item.getOrderStatus());

            List<OrderItemResponse> orderItemResponses = item.getOrderItems().stream().map(i -> {
                OrderItemResponse itemResponse = orderItemMapper.toResponse(i);

                ProductSku sku = i.getProductSku();
                ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());
                boolean rated = ratingRepository.existsByCustomer_IdAndOrderItem_Id(customerId, i.getId());

                itemResponse.setName(spu.getName());
                itemResponse.setProductSkuId(sku.getId());
                itemResponse.setThumbnail(spu.getImage());
                itemResponse.setProductSpuId(spu.getId());
                itemResponse.setVariant(productSkuUtils.getVariant(sku));
                itemResponse.setRated(rated);
                return itemResponse;
            }).collect(Collectors.toList());

            orderResponse.setOrderItems(orderItemResponses);
            return orderResponse;
        });

        return PaginatedResponse.fromSpringPage(responsePage);
    }


    public PaginatedResponse<OrderResponse> getOrdersByStatuses(List<OrderStatus> statuses, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders;
        if (statuses == null || statuses.isEmpty()) {
            orders = orderRepository.findAll(pageable);
        } else {
            orders = orderRepository.findByOrderStatusIn(statuses, pageable);
        }

        Page<OrderResponse> responses = orders.map(item -> {
            OrderResponse orderResponse = orderMapper.toResponse(item);
            orderResponse.setStatus(item.getOrderStatus());

            List<OrderItemResponse> orderItemResponses = item.getOrderItems().stream().map(i -> {
                OrderItemResponse itemResponse = orderItemMapper.toResponse(i);

                ProductSku sku = i.getProductSku();
                ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());

                itemResponse.setName(spu.getName());
                itemResponse.setProductSkuId(sku.getId());
                itemResponse.setThumbnail(spu.getImage());
                itemResponse.setProductSpuId(spu.getId());
                itemResponse.setVariant(productSkuUtils.getVariant(sku));
                return itemResponse;
            }).collect(Collectors.toList());

            orderResponse.setOrderItems(orderItemResponses);
            return orderResponse;
        });

        return PaginatedResponse.fromSpringPage(responses);
    }


    public PaginatedResponse<OrderResponse> searchOrders(OrderSearchCriteria criteria, int page, int size) {
        // 1. Thiết lập phân trang và sắp xếp
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Xây dựng Specification
        Specification<Order> spec = OrderUtils.byCriteria(criteria);

        // 3. Thực hiện truy vấn
        Page<Order> orders = orderRepository.findAll(spec, pageable);

        Page<OrderResponse> responses = orders.map(item -> {
            OrderResponse orderResponse = orderMapper.toResponse(item);
            orderResponse.setStatus(item.getOrderStatus());

            List<OrderItemResponse> orderItemResponses = item.getOrderItems().stream().map(i -> {
                OrderItemResponse itemResponse = orderItemMapper.toResponse(i);

                ProductSku sku = i.getProductSku();
                ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());

                itemResponse.setName(spu.getName());
                itemResponse.setProductSkuId(sku.getId());
                itemResponse.setThumbnail(spu.getImage());
                itemResponse.setProductSpuId(spu.getId());
                itemResponse.setVariant(productSkuUtils.getVariant(sku));
                return itemResponse;
            }).collect(Collectors.toList());

            orderResponse.setOrderItems(orderItemResponses);
            return orderResponse;
        });

        return PaginatedResponse.fromSpringPage(responses);
    }


    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        String customerId = AppUtils.getCustomerIdByJwt();
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        Voucher voucher = null;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            voucher = voucherRepository.findByVoucherCode(request.getVoucherCode()).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));
            if (voucher.getAmount() <= voucher.getUsedCount()) {
                voucher.setStatus(VoucherStatus.Inactive);
                throw new AppException(ErrorCode.NOT_ENOUGH_QUANTITY_VOUCHER);
            }
            if (!(voucher.getStartDate().isBefore(LocalDateTime.now()) && voucher.getEndDate().isAfter(LocalDateTime.now()))) {
                voucher.setStatus(VoucherStatus.Inactive);
                throw new AppException(ErrorCode.VOUCHER_HAS_EXPIRED);
            }
            if (voucherRepository.countUsagePerUser(customerId, voucher.getId()) >= voucher.getMaxUsagePerUser()) {
                throw new AppException(ErrorCode.NOT_ENOUGH_USAGE_VOUCHER);
            }
        }

        Order order = orderMapper.toOrder(request);
        order.setId(UUID.randomUUID().toString());
        order.setCustomer(customer);

        order.setFeeShip(request.getFeeShip());
        order.setOrderStatus(OrderStatus.pending_payment);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest item : request.getOrderItems()) {
            ProductSku sku = productSkuRepository.findById(item.getProductSkuId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

            if (sku.getSkuStock() < item.getQuantity())
                throw new AppException(ErrorCode.NOT_ENOUGH_QUANTITY_PRODUCT);

            sku.setSkuStock(sku.getSkuStock() - item.getQuantity());

            if (sku.getSkuStock() <= 0) {
                sku.setStatus(ProductSku.Status.Inactive);
            }

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
                totalAmount.compareTo(voucher.getMinOrderValue()) > 0) {
            order.setVoucherCode(voucher.getVoucherCode());
            order.setVoucher(voucher);
            BigDecimal percentDiscount = BigDecimal
                    .valueOf(voucher.getDiscountPercent())
                    .multiply(totalAmount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal maxDiscount = voucher.getMaxDiscountValue();

            voucherDiscount = percentDiscount.min(maxDiscount);

            voucher.setUsedCount(voucher.getUsedCount() + 1);

            if (voucher.getAmount() <= voucher.getUsedCount())
                voucher.setStatus(VoucherStatus.Inactive);

        }

        order.setVoucherDiscountAmount(voucherDiscount);

        BigDecimal finalAmount = totalAmount
                .add(request.getFeeShip())
                .subtract(voucherDiscount)
                .subtract(request.getLoyalPoint())
                .setScale(2, RoundingMode.HALF_UP);

        order.setFinalAmount(finalAmount);

        BigDecimal loyalPoint = finalAmount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (customer.getLoyaltyPoints() != null)
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
            itemResponse.setRated(false);
            itemResponse.setProductSpuId(spu.getId());
            orderItemResponses.add(itemResponse);
        });

        response.setOrderItems(orderItemResponses);
        if (request.isBuyFromCart()) {
            Cart cart = cartRepository.findByCustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
            List<CartItem> deleteCartItems = cart.getCartItems();
            Set<String> skus = request.getOrderItems().stream().map(OrderItemRequest::getProductSkuId).collect(Collectors.toSet());
            List<CartItem> itemsToDelete = deleteCartItems.stream()
                    .filter(cartItem -> skus.contains(cartItem.getProductSku().getId()))
                    .toList();

            cart.getCartItems().removeAll(itemsToDelete);
            cartRepository.save(cart);
        }

        return response;
    }

    @Transactional
    public String createVnPayPayment(String orderId, String paymentMethodId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (!order.getOrderStatus().equals(OrderStatus.pending_payment))
            throw new AppException(ErrorCode.ORDER_PAID);

        int amount = order.getFinalAmount().intValue();

        String orderInfo = "DON HANG " + orderId;
        String transactionId = UUID.randomUUID().toString();

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .order(order)
                .amount(order.getFinalAmount())
                .transactionType(TransactionType.payment)
                .status(TransactionStatus.pending)
                .vnpTxnRef(transactionId)
                .paymentMethod(PaymentMethod.builder()
                        .id(paymentMethodId).build())
                .build();

        transactionRepository.save(transaction);

        String returnUrl = ApiPath.FULLURL
                + ApiPath.Payment.ROOT
                + ApiPath.Payment.VNPAY_RETURN
                + "?transactionId=" + transaction.getId();

        return vnPayService.createOrder(amount, orderInfo, transactionId, returnUrl);
    }

    @Transactional
    public int updateStatusTransaction(HttpServletRequest request) {

        int result = vnPayService.orderReturn(request);

        String transactionId = request.getParameter("transactionId");

        Transaction existedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_EXIST));
        String gatewayTransactionId = request.getParameter("vnp_TransactionNo");
        String vnpTxnRefResponse = request.getParameter("vnp_TxnRef"); // Mã tham chiếu Merchant trả về
        String vnpTransactionDate = request.getParameter("vnp_PayDate");
        existedTransaction.setGatewayTransactionId(gatewayTransactionId);
        existedTransaction.setVnpTransactionDate(vnpTransactionDate);
        existedTransaction.setVnpTxnRef(vnpTxnRefResponse);

        Order order = existedTransaction.getOrder();

        switch (result) {
            case 1: {
                existedTransaction.setStatus(TransactionStatus.success);
                order.setOrderStatus(OrderStatus.pending_confirmation);
                break;
            }
            case 0: {
                existedTransaction.setStatus(TransactionStatus.failed);
                order.setOrderStatus(OrderStatus.payment_failed);
                break;
            }
            default:
                throw new AppException(ErrorCode.PAYMENT_INVALID);
        }
        transactionRepository.save(existedTransaction);
        orderRepository.save(order);
        return result;
    }

    @Transactional
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5 phút
    public void checkPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByStatusAndTransactionType(TransactionStatus.pending, TransactionType.payment);
        LocalDateTime now = LocalDateTime.now();

        // Thu thập các Entity đã thay đổi
        List<Transaction> transactionsToUpdate = new ArrayList<>();
        List<Order> ordersToUpdate = new ArrayList<>();
        List<ProductSku> skuItemsToUpdate = new ArrayList<>();

        for (Transaction tx : pendingTransactions) {
            if (tx.getCreateDate().plusMinutes(15).isBefore(now)) {

                // 1. Cập nhật Transaction
                tx.setStatus(TransactionStatus.failed);
                transactionsToUpdate.add(tx);

                // 2. Cập nhật Order
                Order order = tx.getOrder();
                order.setOrderStatus(OrderStatus.payment_failed);
                ordersToUpdate.add(order);

                // 3. Hoàn trả Tồn kho
                order.getOrderItems().forEach(item -> {
                    ProductSku sku = item.getProductSku();
                    sku.setSkuStock(sku.getSkuStock() + item.getQuantity());
                    if(sku.getSkuStock()>0 && sku.getStatus().equals(ProductSku.Status.Inactive)){
                        sku.setStatus(ProductSku.Status.Active);
                    }
                    skuItemsToUpdate.add(sku);
                });

                // 4. Hoàn trả Voucher
                Voucher voucher = order.getVoucher();
                if (voucher != null) {
                    // Voucher cũng là Managed Entity (qua Order), chỉ cần set
                    voucher.setUsedCount(voucher.getUsedCount() - 1);
                    if (voucher.getUsedCount() < voucher.getAmount() && voucher.getStatus().equals(VoucherStatus.Inactive))
                        voucher.setStatus(VoucherStatus.Active);
                }
                Customer customer = order.getCustomer();
                BigDecimal refundLoyalPoint = order.getFinalAmount()
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                customer.setLoyaltyPoints(customer.getLoyaltyPoints().subtract(refundLoyalPoint));
            }
        }

        // 5. Lưu toàn bộ thay đổi sau khi vòng lặp kết thúc
        if (!transactionsToUpdate.isEmpty()) {
            transactionRepository.saveAll(transactionsToUpdate);
        }
        if (!ordersToUpdate.isEmpty()) {
            orderRepository.saveAll(ordersToUpdate);
        }
        // Ghi chú: Việc save SKUItemsToUpdate có thể KHÔNG cần thiết
        // nếu OrderItems được fetch join và SKU được fetch.
        // Tuy nhiên, để đảm bảo (và vì bạn đã có code đó), ta giữ lại:
        if (!skuItemsToUpdate.isEmpty()) {
            productSkuRepository.saveAll(skuItemsToUpdate);
        }
    }

    @Transactional
    public void confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (!order.getOrderStatus().equals(OrderStatus.pending_confirmation))
            throw new AppException(ErrorCode.ORDER_STATUS_INVALID);

        String employeeId = AppUtils.getEmployeeIdByJwt();
        if (employeeId.isEmpty())
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (OrderStatus.pending_confirmation.equals(order.getOrderStatus())) {
            order.setOrderStatus(OrderStatus.confirmed);
            order.setConfirmBy(Employee.builder().id(employeeId).build());
            order.setConfirmDate(LocalDateTime.now());
        } else if (OrderStatus.ready_to_pick.equals(order.getOrderStatus())) {
            order.setOrderStatus(OrderStatus.shipping);
        }
        orderRepository.save(order);
    }

    public GhnPreviewResponse createShipment(String orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (!order.getOrderStatus().equals(OrderStatus.confirmed)) {

            throw new AppException(ErrorCode.ORDER_STATUS_INVALID);

        }

        List<OrderItem> orderItems = order.getOrderItems();
        Map<String, ProductSpu> spuMap = new HashMap<>();

        orderItems.forEach(item -> {
            ProductSpu spu = item.getProductSku().getSpu();
            spuMap.put(item.getId(), spu);
        });

        List<GhnPreviewRequest.GhnItem> ghnItems = orderItems.stream().map(orderItem -> {

            ProductSpu spu = spuMap.get(orderItem.getId());
            ProductSku sku = orderItem.getProductSku();
            GhnPreviewRequest.GhnItem ghnitem = GhnPreviewRequest.GhnItem.builder()
                    .name(spu.getName())
                    .weight(Double.valueOf(sku.getWeight() * 100.0).intValue())
                    .quantity(orderItem.getQuantity())
                    .build();

            return ghnitem;
        }).toList();

        int totalWeight = ghnItems.stream().mapToInt(ghnItem -> ghnItem.getWeight() * ghnItem.getQuantity()).sum();

        GhnPreviewRequest ghnPreviewRequest = GhnPreviewRequest.builder()
                .items(ghnItems)
                .to_ward_code(order.getWardId())
                .to_phone(order.getReceiverPhone())
                .to_name(order.getReceiverName())
                .to_address(order.getShippingAddress())
                .weight(totalWeight)
                .build();

        GhnPreviewResponse response = ghnService.createOrder(ghnPreviewRequest);

        if (response.getData().getOrder_code() != null) {
            order.setOrderStatus(OrderStatus.ready_to_pick);
            orderRepository.save(order);
            return response;
        }
        throw new AppException(ErrorCode.CREATE_SHIPPING_FAIL);
    }

    @Transactional
    public int cancelOrder(String orderId, String paymentMethodId) {
        String customerId = AppUtils.getCustomerIdByJwt();

        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));
//        if (order.getOrderStatus().equals(OrderStatus.ready_to_pick)) {
//            order.setOrderStatus(OrderStatus.pending_cancellation);
//            orderRepository.save(order);
//            return 1; // 1 yêu cầu hủy đã gửi, chờ xác nhận
//        }
        if (order.getOrderStatus().equals(OrderStatus.pending_confirmation) ||
                order.getOrderStatus().equals(OrderStatus.confirmed)) {
            Transaction transaction = transactionRepository
                    .findByOrderIdAndTransactionTypeAndStatus(orderId, TransactionType.payment, TransactionStatus.success)
                    .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_EXIST));


            int result = processVnPayRefund(order, transaction, "AutoRefund");
            switch (result) {

                case 1:
                case 2:
                    List<OrderItem> orderItems = order.getOrderItems();
                    List<ProductSku> skuItems = new ArrayList<>();
                    orderItems.forEach(item -> {
                        int currentStock = item.getProductSku().getSkuStock();
                        ProductSku sku = item.getProductSku();
                        sku.setSkuStock(currentStock + item.getQuantity());
                        skuItems.add(sku);
                    });
                    Voucher voucher = order.getVoucher();
                    if (voucher != null) {
                        voucher.setUsedCount(voucher.getUsedCount() - 1);
                        if(voucher.getUsedCount() < voucher.getAmount() && voucher.getStatus().equals(VoucherStatus.Inactive))
                            voucher.setStatus(VoucherStatus.Active);
                    }

                    order.setOrderStatus(OrderStatus.cancelled);
                    BigDecimal refundLoyalPoint = order.getFinalAmount().divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
                    customer.setLoyaltyPoints(customer.getLoyaltyPoints().subtract(refundLoyalPoint));

                    productSkuRepository.saveAll(skuItems);
                    orderRepository.save(order);
                    return 2;
                case 0:
                case 3:
                    return 3;
            }

        }
        return -1;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Đảm bảo việc tạo transaction là độc lập
    public int processVnPayRefund(Order order, Transaction originalPaymentTransaction, String vnpCreateBy) {
        // 1. Tạo giao dịch hoàn tiền mới với trạng thái PENDING
        String newTransactionId = UUID.randomUUID().toString();
        String refundReason = "HOAN TIEN HUY DON " + order.getId();

        Transaction refTransaction = Transaction.builder()
                .id(newTransactionId)
                .amount(originalPaymentTransaction.getAmount())
                .paymentMethod(originalPaymentTransaction.getPaymentMethod())
                .order(order)
                .parentTransaction(originalPaymentTransaction)
                .transactionType(TransactionType.refund)
                .status(TransactionStatus.pending)
                .vnpTxnRef(newTransactionId)
                .refundReason(refundReason)
                .build();

        refTransaction = transactionRepository.save(refTransaction); // Lưu bản ghi PENDING

        // 2. Chuẩn bị tham số và gọi API hoàn tiền VNPAY
        String originalTransactionDate = originalPaymentTransaction.getVnpTransactionDate();
        String transactionNoGoc = originalPaymentTransaction.getGatewayTransactionId();
        String vnpTxnRef = originalPaymentTransaction.getVnpTxnRef();
        String vnpRequestId = UUID.randomUUID().toString().replace("-", "");
        String orderInfor = refTransaction.getRefundReason();

        // Gọi hàm chuẩn bị dữ liệu và thực thi POST
        String refundApiUrl = vnPayService.refundOrder(
                originalPaymentTransaction.getAmount().intValue(),
                vnpTxnRef,
                originalTransactionDate,
                transactionNoGoc,
                "02", // Loại hoàn tiền: 02 - Hoàn tiền toàn phần
                vnpRequestId,
                orderInfor,
                vnpCreateBy // Tham số linh hoạt truyền vào
        );

        String vnpResponseQueryString = vnPayService.executeVnPayRefundPost(refundApiUrl);
        int refundResult = vnPayService.refundReturn(vnpResponseQueryString);

        // 3. Xử lý kết quả và cập nhật trạng thái giao dịch (chưa cập nhật Order Status)
        switch (refundResult) {
            case 1: // Hoàn tiền thành công ngay
                refTransaction.setStatus(TransactionStatus.success);
                break;

            case 2: // Hoàn tiền đang xử lý
                refTransaction.setStatus(TransactionStatus.pending);
                break;

            case 0: // Hoàn tiền thất bại (VNPAY trả lỗi)
            case 3:
                refTransaction.setStatus(TransactionStatus.failed);
                break;

            case -1: // Lỗi kỹ thuật (ký tự, hash không hợp lệ)
            default:
                refTransaction.setStatus(TransactionStatus.failed);
                transactionRepository.save(refTransaction);
                throw new AppException(ErrorCode.PAYMENT_INVALID);
        }

        transactionRepository.save(refTransaction); // Cập nhật trạng thái cuối cùng của giao dịch hoàn tiền

        // Trả về kết quả để OrderService quyết định cập nhật Order Status
        return refundResult;
    }

//    @Transactional
//    public int confirmCancelOrder(String orderId) {
//
//        String employeeId = AppUtils.getEmployeeIdByJwt();
//
//        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
//
//        Transaction originalTransaction = transactionRepository.findByOrderIdAndTransactionTypeAndStatus(orderId, TransactionType.payment, TransactionStatus.success)
//                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_EXIST));
//
//        if (!order.getOrderStatus().equals(OrderStatus.pending_cancellation))
//            throw new AppException(ErrorCode.ORDER_STATUS_INVALID);
//
//        int result = processVnPayRefund(order, originalTransaction, employeeId);
//
//        switch (result) {
//            case 1:
//                order.setOrderStatus(OrderStatus.cancelled);
//                orderRepository.save(order);
//                return 2;
//            case 2:
//                order.setOrderStatus(OrderStatus.cancelled);
//                orderRepository.save(order);
//                return 0;
//            case 0:
//            case 3:
//                return 3;
//        }
//        return -1;
//    }


    public List<Map<String, String>> getAllOrderStatus() {
        return Arrays.stream(OrderStatus.values())
                .map(s -> Map.of(
                        "code", s.name(),
                        "label", s.getLabel()
                )).toList();
    }

    @Scheduled(fixedDelayString = "30000") // 30 giây
    @Transactional
    public void simulateGhnWebhook() {
        // Tìm các đơn hàng đang ở trạng thái có thể được GHN cập nhật
        List<OrderStatus> statusesToUpdate = Arrays.asList(
                OrderStatus.ready_to_pick,
                OrderStatus.shipping
        );

        List<Order> ordersToProcess = orderRepository.findByOrderStatusIn(statusesToUpdate);

        for (Order order : ordersToProcess) {
            if (random.nextDouble() < 0.7) { // 70% cơ hội cập nhật
                OrderStatus nextStatus = getGhnNextStatus(order.getOrderStatus());

                if (isValidGhnStatusTransition(order.getOrderStatus(), nextStatus)) {
                    order.setOrderStatus(nextStatus);
                    order.setUpdateDate(LocalDateTime.now());
                    log.info("[GHN SIM] Đơn hàng ID: {} - Cập nhật từ {} -> {}",
                            order.getId(), order.getOrderStatus(), nextStatus);
                }
            }
        }
    }

    private OrderStatus getGhnNextStatus(OrderStatus currentStatus) {
        if (currentStatus == OrderStatus.ready_to_pick) {
            return OrderStatus.shipping; // Luôn chuyển sang shipping
        }
        if (currentStatus == OrderStatus.shipping) {
            // Ngẫu nhiên chuyển sang Delivered, Cancelled, hoặc Returned
            int choice = random.nextInt(3);
            if (choice == 0) return OrderStatus.delivered;
            if (choice == 1) return OrderStatus.cancelled;
            return OrderStatus.returned;
        }
        return null;
    }

    private boolean isValidGhnStatusTransition(OrderStatus current, OrderStatus next) {
        // Luồng chính: ready_to_pick -> shipping -> delivered
        if (current == OrderStatus.ready_to_pick && next == OrderStatus.shipping) {
            return true;
        }
        if (current == OrderStatus.shipping && next == OrderStatus.delivered) {
            return true;
        }

        // Nếu GHN có thể gửi trạng thái hủy (cancelled) hoặc trả hàng (returned)
        // từ trạng thái SHIPPING, ta cũng cho phép:
        if (current == OrderStatus.shipping &&
                (next == OrderStatus.cancelled || next == OrderStatus.returned || next == OrderStatus.partially_returned)) {
            return true;
        }

        // Các trạng thái khác không được cập nhật qua mô phỏng GHN này
        return false;
    }
}