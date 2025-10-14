package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @Query("""
        SELECT a 
        FROM Activity a
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(a.type) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:accountId IS NULL OR a.accountId = :accountId)
          AND (:modelId IS NULL OR a.robotModelId = :modelId)
          AND (:status IS NULL OR a.status = :status)
          AND (a.status <> 0)
    """)
    Page<Activity> searchActivities(
            @Param("keyword") String keyword,
            @Param("accountId") UUID accountId,
            @Param("modelId") UUID modelId,
            @Param("status") Integer status,
            Pageable pageable
    );

    Optional<Activity> findByNameIgnoreCaseAndRobotModelIdAndStatusNot(String name, UUID robotModelId, Integer status);

    List<Activity> findAllByTypeIgnoreCaseAndRobotModelIdAndStatusNot(String type,  UUID robotModelId, Integer status);


    Page<Activity> findAllByAccountIdAndRobotModelIdAndStatusNot(UUID accountId, UUID robotModelId, Integer status, Pageable pageable);
}
