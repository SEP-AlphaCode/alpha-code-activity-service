package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Action;
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
                WHERE (:name IS NULL OR :name = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:code IS NULL OR :code = '' OR LOWER(a.code) LIKE LOWER(CONCAT('%', :code, '%')))
                  AND (:description IS NULL OR :description = '' OR LOWER(a.description) LIKE LOWER(CONCAT('%', :description, '%')))
                  AND (:status IS NULL OR a.status = :status)
                  AND (:status <> 0)
                  AND (:canInterrupt IS NULL OR a.canInterrupt = :canInterrupt)
                  AND (:duration IS NULL OR a.duration = :duration)
            """)
    Page<Action> searchActions(
            @Param("name") String name,
            @Param("code") String code,
            @Param("description") String description,
            @Param("status") Integer status,
            @Param("canInterrupt") Boolean canInterrupt,
            @Param("duration") Integer duration,
            Pageable pageable
    );

    Optional<Action> findByCodeIgnoreCase(String code);

    Optional<Action> findByNameIgnoreCase(String name);

    Optional<Action> findByRobotModelId(UUID robotModelId);
}
