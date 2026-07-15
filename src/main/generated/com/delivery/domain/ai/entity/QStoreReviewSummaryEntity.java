package com.delivery.domain.ai.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStoreReviewSummaryEntity is a Querydsl query type for StoreReviewSummaryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreReviewSummaryEntity extends EntityPathBase<StoreReviewSummaryEntity> {

    private static final long serialVersionUID = -1324247150L;

    public static final QStoreReviewSummaryEntity storeReviewSummaryEntity = new QStoreReviewSummaryEntity("storeReviewSummaryEntity");

    public final DateTimePath<java.time.LocalDateTime> generatedAt = createDateTime("generatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> reviewCountAtGeneration = createNumber("reviewCountAtGeneration", Long.class);

    public final ComparablePath<java.util.UUID> storeId = createComparable("storeId", java.util.UUID.class);

    public final StringPath summaryText = createString("summaryText");

    public QStoreReviewSummaryEntity(String variable) {
        super(StoreReviewSummaryEntity.class, forVariable(variable));
    }

    public QStoreReviewSummaryEntity(Path<? extends StoreReviewSummaryEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStoreReviewSummaryEntity(PathMetadata metadata) {
        super(StoreReviewSummaryEntity.class, metadata);
    }

}

