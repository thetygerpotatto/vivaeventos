package com.vivaeventos.order_service.domain.repository;

import com.vivaeventos.order_service.domain.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IDiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {

    Optional<DiscountCode> findByCode(String code);
}