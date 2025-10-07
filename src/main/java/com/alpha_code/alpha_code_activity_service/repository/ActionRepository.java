package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Action;
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
public interface ActionRepository extends JpaRepository<Action, UUID> {

    @Query("""
                SELECT a 
                FROM Action a
                WHERE (:robotModelId IS NULL OR a.robotModelId = :robotModelId)
                  AND (:name IS NULL OR :name = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:code IS NULL OR :code = '' OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
                  AND (:status IS NULL OR a.status = :status)
                  AND (a.status <> 0)
                  AND (:canInterrupt IS NULL OR a.canInterrupt = :canInterrupt)
                  AND (:duration IS NULL OR a.duration = :duration)
            """)
    Page<Action> searchActions(
            @Param("robotModelId") UUID robotModelId,
            @Param("name") String name,
            @Param("code") String code,
            @Param("status") Integer status,
            @Param("canInterrupt") Boolean canInterrupt,
            @Param("duration") Integer duration,
            Pageable pageable
    );

    Optional<Action> findByCodeIgnoreCaseAndStatusNot(String code, Integer status);

    Optional<Action> findByNameIgnoreCaseAndStatusNot(String name, Integer status);

    Page<Action> findByRobotModelIdAndStatusNot(UUID robotModelId, Integer status, Pageable pageable);
}
