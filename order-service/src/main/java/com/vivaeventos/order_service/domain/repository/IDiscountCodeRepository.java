package com.vivaeventos.order_service.domain.repository;

import com.vivaeventos.order_service.domain.model.DiscountCode;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {
}
