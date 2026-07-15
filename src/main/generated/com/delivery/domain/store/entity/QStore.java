package com.delivery.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = -1542527392L;

    public static final QStore store = new QStore("store");

    public final com.delivery.common.base.QBaseEntity _super = new com.delivery.common.base.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final NumberPath<Double> averageRating = createNumber("averageRating", Double.class);

    public final ComparablePath<java.util.UUID> categoryId = createComparable("categoryId", java.util.UUID.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final StringPath description = createString("description");

    public final BooleanPath isOpen = createBoolean("isOpen");

    public final NumberPath<Integer> minOrderAmount = createNumber("minOrderAmount", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final ComparablePath<java.util.UUID> regionId = createComparable("regionId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> storeId = createComparable("storeId", java.util.UUID.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QStore(String variable) {
        super(Store.class, forVariable(variable));
    }

    public QStore(Path<? extends Store> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStore(PathMetadata metadata) {
        super(Store.class, metadata);
    }

}

