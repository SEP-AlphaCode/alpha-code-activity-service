package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.OsmoCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OsmoCardRepository extends JpaRepository<OsmoCard, UUID> {
    Page<OsmoCard> findAllByStatus(Integer status, Pageable pageable);

    OsmoCard findOsmoCardByColorAndStatusNot(String color, Integer status);
}
