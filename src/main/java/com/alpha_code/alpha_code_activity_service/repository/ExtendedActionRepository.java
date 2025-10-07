package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.ExtendedAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface  ExtendedActionRepository extends JpaRepository<ExtendedAction, UUID> {
    Optional<ExtendedAction> findByCodeIgnoreCaseAndStatusNot(String code, Integer status);

    Optional<ExtendedAction> findByNameIgnoreCaseAndStatusNot(String name, Integer status);

    Page<ExtendedAction> findByRobotModelIdAndStatusNot(UUID robotModelId, Integer status, Pageable pageable);

    @Query("""
       SELECT ea FROM ExtendedAction ea
       WHERE :searchTerm IS NULL OR 
             LOWER(ea.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(ea.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             CAST(ea.status AS string) LIKE CONCAT('%', :searchTerm, '%')
       ORDER BY ea.name
       """)
    Page<ExtendedAction> searchExtendedActions(@Param("searchTerm") String searchTerm, Pageable pageable);


}
