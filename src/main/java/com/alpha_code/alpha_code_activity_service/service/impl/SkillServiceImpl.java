package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.dto.SkillDto;
import com.alpha_code.alpha_code_activity_service.entity.Skill;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.SkillMapper;
import com.alpha_code.alpha_code_activity_service.repository.SkillRepository;
import com.alpha_code.alpha_code_activity_service.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;

    @Override
    @Cacheable(value = "skills_list", key = "{#page, #size, #keyword}")
    public PagedResult<SkillDto> searchSkills(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Skill> actions = skillRepository.searchSkills(searchTerm, pageable);
        return new PagedResult<>(actions.map(SkillMapper::toDto));
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "skills", key = "#id")
    public SkillDto getSkillById(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với id: " + id));
        return SkillMapper.toDto(skill);
    }

    @Override
    @Cacheable(value = "skills", key = "#name")
    public SkillDto getSkillByName(String name) {
        Skill skill = skillRepository.findByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với name: " + name));
        return SkillMapper.toDto(skill);
    }

    @Override
    @Cacheable(value = "skills", key = "#code")
    public SkillDto getSkillByCode(String code) {
        Skill skill = skillRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với code: " + code));
        return SkillMapper.toDto(skill);
    }

    @Override
    @Cacheable(value = "skills", key = "{#robotModelId, #page, #size}")
    public PagedResult<SkillDto> getSkillByRobotModelId(UUID robotModelId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<Skill> pageResult =
                skillRepository.findByRobotModelIdAndStatusNot(robotModelId, 0, pageable);

        // Map sang DTO
        Page<SkillDto> dtoPage = pageResult.map(SkillMapper::toDto);

        // Trả về dạng PagedResult (custom wrapper)
        return new PagedResult<>(dtoPage);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"skills_list"}, allEntries = true)
    public SkillDto createSkill(SkillDto skillDto) {

        var existed = skillRepository.findByCodeIgnoreCaseAndStatusNot(skillDto.getCode(), 0);
        if (existed.isPresent()) {
            throw new ConflictException("Đã tồn tại skill với code: " + skillDto.getCode());
        }

        existed = skillRepository.findByNameIgnoreCaseAndStatusNot(skillDto.getName(), 0);
        if (existed.isPresent()) {
            throw new ResourceNotFoundException("Đã tồn tại skill với tên: " + skillDto.getName());
        }
        Skill skill = SkillMapper.toEntity(skillDto);
        skill.setCreatedDate(LocalDateTime.now());
        skill.setLastUpdated(null);
        skill.setStatus(1); // Mặc định là active
        Skill savedSkill = skillRepository.save(skill);
        return SkillMapper.toDto(savedSkill);
    }

    @Override
    @Transactional
    @CachePut(value = "skills", key = "#id")
    @CacheEvict(value = {"skills_list"}, allEntries = true)
    public SkillDto updateSkill(UUID id, SkillDto skillDto) {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với id: " + id));

        existingSkill.setName(skillDto.getName());
        existingSkill.setCode(skillDto.getCode());
        existingSkill.setLastUpdated(LocalDateTime.now());
        existingSkill.setStatus(skillDto.getStatus());
        existingSkill.setIcon(skillDto.getIcon());
        existingSkill.setRobotModelId(skillDto.getRobotModelId());

        Skill updatedSkill = skillRepository.save(existingSkill);
        return SkillMapper.toDto(updatedSkill);
    }

    @Override
    @Transactional
    @CachePut(value = "skills", key = "#id")
    @CacheEvict(value = {"skills_list"}, allEntries = true)
    public SkillDto patchUpdateSkill(UUID id, SkillDto skillDto) {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với id: " + id));

        if (skillDto.getName() != null) {
            existingSkill.setName(skillDto.getName());
        }

        if (skillDto.getCode() != null) {
            existingSkill.setCode(skillDto.getCode());
        }

        if (skillDto.getStatus() != null) {
            existingSkill.setStatus(skillDto.getStatus());
        }

        if (skillDto.getIcon() != null) {
            existingSkill.setIcon(skillDto.getIcon());
        }

        if (skillDto.getRobotModelId() != null) {
            existingSkill.setRobotModelId(skillDto.getRobotModelId());
        }

        existingSkill.setLastUpdated(LocalDateTime.now());

        Skill updatedSkill = skillRepository.save(existingSkill);
        return SkillMapper.toDto(updatedSkill);

    }

    @Override
    @Transactional
    @CachePut(value = "skills", key = "#id")
    @CacheEvict(value = {"skills_list"}, allEntries = true)
    public SkillDto changeSkillStatus(UUID id, Integer status) {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với id: " + id));
        existingSkill.setStatus(status);
        existingSkill.setLastUpdated(LocalDateTime.now());
        Skill updatedSkill = skillRepository.save(existingSkill);
        return SkillMapper.toDto(updatedSkill);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"skills", "skills_list"}, key = "#id", allEntries = true)
    public String deleteSkill(UUID id) {
        Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy skill với id: " + id));

        existingSkill.setStatus(0);
        existingSkill.setLastUpdated(LocalDateTime.now());
        skillRepository.save(existingSkill);
        return "skill deleted successfully";
    }
}
