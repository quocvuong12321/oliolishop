package com.oliolishop.oliolishop.entity;


import com.oliolishop.oliolishop.enums.TransactionStatus;
import com.oliolishop.oliolishop.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.transaction.annotation.TransactionAnnotationParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name= "payment_method_id")
    PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transaction_id")
    Transaction parentTransaction; // Liên kết tự tham chiếu đến giao dịch PAYMENT gốc (cho REFUND/CANCEL)

    BigDecimal amount;

    @Column(name = "transaction_type", columnDefinition = "ENUM('PAYMENT','REFUND','CANCEL')")
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    @Column(name = "vnp_txn_ref")
    String vnpTxnRef; // Mã tham chiếu của Merchant (vnp_TxnRef) - Được sử dụng để gửi VNPAY

    @Column(name = "gateway_transaction_id")
    String gatewayTransactionId; // Mã giao dịch VNPAY (vnp_TransactionNo)

    @Column(name = "refund_reason")
    String refundReason; // Lý do hoàn tiền/hủy giao dịch

    @Column(name = "vnp_response_code")
     String vnpResponseCode;
    @Column(name = "vnp_transaction_date")
    String vnpTransactionDate;


    LocalDateTime createDate;
    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
    }

}
