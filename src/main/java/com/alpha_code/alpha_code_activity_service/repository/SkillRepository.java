package com.alpha_code.alpha_code_activity_service.repository;

import com.alpha_code.alpha_code_activity_service.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findByCodeIgnoreCaseAndStatusNot(String code, Integer status);

    Optional<Skill> findByNameIgnoreCaseAndStatusNot(String name, Integer status);

    Page<Skill> findByRobotModelIdAndStatusNot(UUID robotModelId, Integer status, Pageable pageable);

    @Query("""
       SELECT s FROM Skill s
       WHERE :keyword IS NULL OR 
             LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             CAST(s.status AS string) LIKE CONCAT('%', :searchTerm, '%')
       ORDER BY s.name
       """)
    Page<Skill> searchSkills(@Param("searchTerm") String searchTerm, Pageable pageable);

}
