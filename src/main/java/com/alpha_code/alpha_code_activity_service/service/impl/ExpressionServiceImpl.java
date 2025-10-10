package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.DanceDto;
import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.Expression;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.mapper.DanceMapper;
import com.alpha_code.alpha_code_activity_service.mapper.ExpressionMapper;
import com.alpha_code.alpha_code_activity_service.repository.ExpressionRepository;
import com.alpha_code.alpha_code_activity_service.service.ExpressionService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class ExpressionServiceImpl implements ExpressionService {

    private final ExpressionRepository repository;
    private final RobotServiceClient robotServiceClient;

    @Override
    @Cacheable(value = "expressions_list", key = "{#page, #size, #name, #code, #status, #robotModelId}")
    public PagedResult<ExpressionDto> getAll(int page, int size, String name, String code, Integer status, UUID robotModelId) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Expression> pageResult;

        pageResult = repository.getAll(name, code, status, robotModelId, pageable);

        List<ExpressionDto> dtos = pageResult.getContent().stream()
                .map(ExpressionMapper::toDto)
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
        Page<ExpressionDto> dtoPage = new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
        return new PagedResult<>(dtoPage);
    }

    @Override
    @Cacheable(value = "expressions", key = "#id")
    public ExpressionDto getById(UUID id) {
        var expression = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        return ExpressionMapper.toDto(expression);
    }

    @Override
    @Transactional
    @CacheEvict(value = "expressions_list", allEntries = true)
    public ExpressionDto create(ExpressionDto dto) {
        var valid = repository.getByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (valid.isPresent()) {
            throw new ResourceNotFoundException("Expression name already exists");
        }
        valid = repository.getByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (valid.isPresent()) {
            throw new ResourceNotFoundException("Expression code already exists");
        }

        var expression = ExpressionMapper.toEntity(dto);
        expression.setCreatedDate(LocalDateTime.now());

        var savedExpression = repository.save(expression);
        return ExpressionMapper.toDto(savedExpression);
    }

    @Override
    @Transactional
    @CacheEvict(value = "expressions_list", allEntries = true)
    @CachePut(value = "expressions", key = "#id")
    public ExpressionDto update(UUID id, ExpressionDto dto) {
        var valid = repository.getByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Expression name already exists");
        }
        valid = repository.getByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Expression code already exists");
        }

        var expression = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));
        expression.setName(dto.getName());
        expression.setCode(dto.getCode());
        expression.setImageUrl(dto.getImageUrl());
        expression.setStatus(dto.getStatus());
        expression.setRobotModelId(dto.getRobotModelId());
        expression.setLastUpdated(LocalDateTime.now());

        var savedExpression = repository.save(expression);
        return ExpressionMapper.toDto(savedExpression);
    }

    @Override
    @Transactional
    @CacheEvict(value = "expressions_list", allEntries = true)
    @CachePut(value = "expressions", key = "#id")
    public ExpressionDto patch(UUID id, ExpressionDto dto) {
        var valid = repository.getByNameIgnoreCaseAndStatusNot(dto.getName(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Expression name already exists");
        }
        valid = repository.getByCodeIgnoreCaseAndStatusNot(dto.getCode(), 0);
        if (valid.isPresent() && valid.get().getId() != id) {
            throw new ResourceNotFoundException("Expression code already exists");
        }

        var expression = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        if (dto.getName() != null) {
            expression.setName(dto.getName());
        }
        if (dto.getCode() != null) {
            expression.setCode(dto.getCode());
        }
        if (dto.getImageUrl() != null) {
            expression.setImageUrl(dto.getImageUrl());
        }
        if (dto.getStatus() != null) {
            expression.setStatus(dto.getStatus());
        }
        if (dto.getRobotModelId() != null) {
            expression.setRobotModelId(dto.getRobotModelId());
        }

        expression.setLastUpdated(LocalDateTime.now());

        var savedExpression = repository.save(expression);
        return ExpressionMapper.toDto(savedExpression);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"expressions_list", "expressions"}, allEntries = true)
    public String delete(UUID id) {
        var expression = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        expression.setStatus(0);
        expression.setLastUpdated(LocalDateTime.now());

        repository.save(expression);
        return "Expression deleted successfully";
    }

    @Override
    @Cacheable(value = "expressions", key = "#name")
    public ExpressionDto getByName(String name) {
        var expression = repository.getByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        return ExpressionMapper.toDto(expression);
    }

    @Override
    @Cacheable(value = "expressions", key = "#code")
    public ExpressionDto getByCode(String code) {
        var expression = repository.getByCodeIgnoreCaseAndStatusNot(code, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        return ExpressionMapper.toDto(expression);
    }

    @Override
    @Cacheable(value = "expressions", key = "#robotModelId")
    public List<ExpressionDto> getByRobotModelId(UUID robotModelId) {
        return repository.getByRobotModelIdAndStatusNot(robotModelId,0)
                .stream()
                .map(ExpressionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"expressions_list"}, allEntries = true)
    @CachePut(value = "expressions", key = "#id")
    public ExpressionDto changeStatus(UUID id, Integer status) {
        var expression = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expression not found"));

        expression.setStatus(status);
        expression.setLastUpdated(LocalDateTime.now());

        repository.save(expression);
        return ExpressionMapper.toDto(expression);
    }
}
