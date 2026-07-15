package com.delivery.domain.payment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 218781888L;

    public static final QPayment payment = new QPayment("payment");

    public final com.delivery.common.base.QBaseEntity _super = new com.delivery.common.base.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> canceledAt = createDateTime("canceledAt", java.time.LocalDateTime.class);

    public final StringPath cancelReason = createString("cancelReason");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> orderId = createComparable("orderId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> paymentAmount = createNumber("paymentAmount", Integer.class);

    public final ComparablePath<java.util.UUID> paymentId = createComparable("paymentId", java.util.UUID.class);

    public final EnumPath<PaymentMethod> paymentMethod = createEnum("paymentMethod", PaymentMethod.class);

    public final EnumPath<PaymentStatus> paymentStatus = createEnum("paymentStatus", PaymentStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

