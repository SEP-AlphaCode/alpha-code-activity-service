package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Dance;
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
public interface DanceRepository extends JpaRepository<Dance, UUID> {
    @Query("""
            SELECT d
            FROM Dance d
            WHERE (:name IS NULL OR :name = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:code IS NULL OR :code = '' OR LOWER(d.code) LIKE LOWER(CONCAT('%', :code, '%')))
            AND (:status IS NULL OR d.status = :status)
            AND (:status <> 0)
            AND (:robotModelId IS NULL OR d.robotModelId = :robotModelId)
            """)
    Page<Dance> searchDances(
            @Param("name") String name,
            @Param("code") String code,
            @Param("status") Integer status,
            @Param("robotModelId") UUID robotModelId,
            Pageable pageable
    );

    Optional<Dance> getDanceByCodeIgnoreCaseAndStatusNot(String code, Integer status);

    Optional<Dance> getDanceByNameIgnoreCaseAndStatusNot(String name, Integer status);

    List<Dance> findAllByRobotModelIdAndStatusNot(UUID robotModelId, Integer status);

}
