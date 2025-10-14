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

    BigDecimal amount;

    @Column(name = "transaction_type", columnDefinition = "ENUM('PAYMENT','REFUND','CANCEL')")
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    TransactionStatus status;

    String gatewayTransactionId;

    @CreationTimestamp
    LocalDateTime createDate;

}
