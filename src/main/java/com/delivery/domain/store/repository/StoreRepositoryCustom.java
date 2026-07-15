package com.delivery.domain.store.repository;

import com.delivery.domain.store.entity.Store;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepositoryCustom {
    Page<Store> searchStores(UUID categoryId, String name, Pageable pageable);
}
