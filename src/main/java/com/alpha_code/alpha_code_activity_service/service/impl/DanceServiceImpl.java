package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ActionDto;
import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Dance;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.mapper.ActionMapper;
import com.alpha_code.alpha_code_activity_service.mapper.DanceMapper;
import com.alpha_code.alpha_code_activity_service.repository.DanceRepository;
import com.alpha_code.alpha_code_activity_service.service.DanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import robot.Robot;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DanceServiceImpl implements DanceService {

    private final DanceRepository repository;
    private final RobotServiceClient robotServiceClient;

    @Override
    @Cacheable(value = "dances_list", key = "{#page, #size, #name, #code, #status, #robotModelId}")
    public PagedResult<DanceDto> getAll(int page, int size, String name, String code, Integer status, UUID robotModelId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Dance> pageResult;

        pageResult = repository.searchDances(name, code, status, robotModelId, pageable);

        List<DanceDto> dtos = pageResult.getContent().stream()
                .map(DanceMapper::toDto)
                .toList();

        // Nếu không có dữ liệu → trả rỗng
        if (dtos.isEmpty()) {
            return new PagedResult<>(Page.empty(pageable));
        }

        // Lấy danh sách modelId duy nhất
        List<String> modelIds = dtos.stream()
                .map(action -> action.getRobotModelId().toString())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Gọi gRPC để lấy thông tin model
        Map<String, Robot.RobotModelInformation> modelMap = Collections.emptyMap();
        try {
            modelMap = robotServiceClient.getRobotModelsByIds(modelIds);
        } catch (Exception e) {
            log.error("Failed to fetch robot models via gRPC", e);
        }

        // Gán robotModelName cho từng DTO
        Map<String, Robot.RobotModelInformation> finalModelMap = modelMap;
        dtos.forEach(dto -> {
            Robot.RobotModelInformation model = finalModelMap.get(dto.getRobotModelId().toString());
            dto.setRobotModelName(model != null ? model.getName() : "Unknown");
        });

        //  Trả kết quả phân trang
        Page<DanceDto> dtoPage = new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
        return new PagedResult<>(dtoPage);
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

        existed.setName(dto.getName());
        existed.setDuration(dto.getDuration());
        existed.setDescription(dto.getDescription());
        existed.setType(dto.getType());
        existed.setCode(dto.getCode());
        existed.setIcon(dto.getIcon());
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

        if (dto.getIcon() != null){
            existed.setIcon(dto.getIcon());
        }

        if (dto.getStatus() != null){
            existed.setStatus(dto.getStatus());
        }

        if(dto.getType() != null){
            existed.setType(dto.getType());
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
