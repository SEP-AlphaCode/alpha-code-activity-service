package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Dance;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.DanceMapper;
import com.alpha_code.alpha_code_activity_service.repository.DanceRepository;
import com.alpha_code.alpha_code_activity_service.service.DanceService;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DanceServiceImpl implements DanceService {

    private final DanceRepository repository;

    @Override
    @Cacheable(value = "dances_list", key = "{#page, #size, #name, #code, #description, #status, #robotModelId}")
    public PagedResult<DanceDto> getAll(int page, int size, String name, String code, String description, Integer status, UUID robotModelId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Dance> pageResult;

        pageResult = repository.searchDances(name, code, description, status, robotModelId, pageable);

        return new PagedResult<>(pageResult.map(DanceMapper::toDto));
    }

    @Override
    @Cacheable(value = "dances", key = "#id")
    public DanceDto getById(UUID id) {
        var dance = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        return DanceMapper.toDto(dance);
    }

    @Override
    @Cacheable(value = "dances", key = "#code")
    public DanceDto getDanceByCode(String code) {
        var dance = repository.getDanceByCodeIgnoreCaseAndStatusNot(code, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        return DanceMapper.toDto(dance);
    }

    @Override
    @Cacheable(value = "dances", key = "#name")
    public DanceDto getDanceByName(String name) {
        var dance = repository.getDanceByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        return DanceMapper.toDto(dance);
    }

    @Override
    @Cacheable(value = "dances", key = "#robotModelId")
    public List<DanceDto> getAllByRobotModelId(UUID robotModelId) {
        return repository.findAllByRobotModelIdAndStatusNot(robotModelId, 0)
                .stream()
                .map(DanceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "dances_list", allEntries = true)
    public DanceDto create(DanceDto dto) {

        var existed = repository.getDanceByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (existed.isPresent()) {
            throw new ResourceNotFoundException("Dance code already exists");
        }
        existed = repository.getDanceByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (existed.isPresent()) {
            throw new ResourceNotFoundException("Dance name already exists");
        }

        var dance = DanceMapper.toEntity(dto);
        dance.setCreatedDate(LocalDateTime.now());

        Dance savedDance = repository.save(dance);
        return DanceMapper.toDto(savedDance);
    }

    @Override
    @Transactional
    @CacheEvict(value = "dances_list", allEntries = true)
    @CachePut(value = "dances", key = "#id")
    public DanceDto update(UUID id, DanceDto dto) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        var valid = repository.getDanceByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Dance code already exists");
        }
        valid = repository.getDanceByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Dance name already exists");
        }

        existed.setName(dto.getName());
        existed.setDuration(dto.getDuration());
        existed.setDescription(dto.getDescription());
        existed.setCode(dto.getCode());
        existed.setStatus(dto.getStatus());
        existed.setLastUpdated(LocalDateTime.now());

        Dance savedDance = repository.save(existed);
        return  DanceMapper.toDto(savedDance);

    }

    @Override
    @Transactional
    @CacheEvict(value = "dances_list", allEntries = true)
    @CachePut(value = "dances", key = "#id")
    public DanceDto patchUpdate(UUID id, DanceDto dto) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        var valid = repository.getDanceByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Dance code already exists");
        }
        valid = repository.getDanceByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Dance name already exists");
        }

        if (dto.getName() != null){
            existed.setName(dto.getName());
        }
        if (dto.getDuration() != null){
            existed.setDuration(dto.getDuration());
        }
        if (dto.getDescription() != null){
            existed.setDescription(dto.getDescription());
        }
        if (dto.getCode() != null){
            existed.setCode(dto.getCode());
        }
        if (dto.getStatus() != null){
            existed.setStatus(dto.getStatus());
        }

        existed.setLastUpdated(LocalDateTime.now());

        Dance savedDance = repository.save(existed);
        return  DanceMapper.toDto(savedDance);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"dances_list", "dances"}, allEntries = true)
    public String delete(UUID id) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        existed.setStatus(0);
        repository.save(existed);
        return "Delete dance successfully";
    }

    @Override
    @Transactional
    @CacheEvict(value = {"dances_list"}, allEntries = true)
    @CachePut(value = "dances", key = "#id")
    public DanceDto changeStatus(UUID id, Integer status) {
        var existed = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dance not found"));

        existed.setStatus(status);
        existed.setLastUpdated(LocalDateTime.now());

        Dance savedDance = repository.save(existed);
        return DanceMapper.toDto(savedDance);
    }
}
