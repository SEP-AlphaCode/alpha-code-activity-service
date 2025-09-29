package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Expression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, UUID> {

    @Query("""
            SELECT e 
            FROM Expression e
            WHERE (:name IS NULL or :name = '' or LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:code IS NULL or :code = '' or LOWER(e.code) LIKE LOWER(CONCAT('%', :code, '%')))
            AND (:status IS NULL or e.status = :status)
            AND (:status <> 0)
            AND (:robotModelId IS NULL or e.robotModelId = :robotModelId)
            """)
    Page<Expression> getAll(@Param("name") String name,
                            @Param("code") String code,
                            @Param("status") Integer status,
                            @Param("robotModelId") UUID robotModelId,
                            Pageable pageable);

    Optional<Expression> getByNameIgnoreCaseAndStatusNot(String name, Integer status);

    Optional<Expression> getByCodeIgnoreCaseAndStatusNot(String code, Integer status);

    List<Expression> getByRobotModelIdAndStatusNot(UUID robotModelId, Integer status);
}
