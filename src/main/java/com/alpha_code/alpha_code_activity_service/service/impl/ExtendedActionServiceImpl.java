package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.ExtendedActionDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.ExtendedAction;
import com.alpha_code.alpha_code_activity_service.exception.ConflictException;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.grpc.client.RobotServiceClient;
import com.alpha_code.alpha_code_activity_service.mapper.ExtendedActionMapper;
import com.alpha_code.alpha_code_activity_service.repository.ExtendedActionRepository;
import com.alpha_code.alpha_code_activity_service.service.ExtendedActionService;
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
public class ExtendedActionServiceImpl implements ExtendedActionService {
    private final ExtendedActionRepository extendedActionRepository;
    private final RobotServiceClient robotServiceClient;


    @Override
    @Cacheable(value = "extended_actions_list", key = "{#page, #size, #searchTerm != null ? #searchTerm.trim() : ''}")
    public PagedResult<ExtendedActionDto> searchExtendedActions(int page, int size, String searchTerm) {
        log.debug("Searching extended actions: page={}, size={}, searchTerm='{}'", page, size, searchTerm);

        // Chuẩn hóa keyword và pageable
        String keyword = (searchTerm == null || searchTerm.trim().isEmpty()) ? "" : searchTerm.trim();
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);

        //  Query dữ liệu từ DB
        Page<ExtendedAction> actions = extendedActionRepository.searchExtendedActions(keyword, pageable);
        List<ExtendedActionDto> dtos = actions.getContent().stream()
                .map(ExtendedActionMapper::toDto)
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
        Page<ExtendedActionDto> dtoPage = new PageImpl<>(dtos, pageable, actions.getTotalElements());
        return new PagedResult<>(dtoPage);
    }



    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "extended_actions", key = "#id")
    public ExtendedActionDto getExtendedActionById(UUID id) {
        ExtendedAction extendedAction = extendedActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Extended Action với id: " + id));
        return ExtendedActionMapper.toDto(extendedAction);
    }

    @Override
    @Cacheable(value = "extended_actions", key = "#name")
    public ExtendedActionDto getExtendedActionByName(String name) {
        ExtendedAction extendedAction = extendedActionRepository.findByNameIgnoreCaseAndStatusNot(name, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Extended Action với name: " + name));
        return ExtendedActionMapper.toDto(extendedAction);
    }

    @Override
    @Cacheable(value = "extended_actions", key = "#code")
    public ExtendedActionDto getExtendedActionByCode(String code) {
        ExtendedAction extendedAction = extendedActionRepository.findByCodeIgnoreCaseAndStatusNot(code, 0)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Extended Action với code: " + code));
        return ExtendedActionMapper.toDto(extendedAction);
    }

    @Override
    @Cacheable(value = "extended_actions", key = "{#robotModelId, #page, #size}")
    public PagedResult<ExtendedActionDto> getExtendedActionByRobotModelId(UUID robotModelId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Page<ExtendedAction> pageResult =
                extendedActionRepository.findByRobotModelIdAndStatusNot(robotModelId, 0, pageable);

        // Map sang DTO
        Page<ExtendedActionDto> dtoPage = pageResult.map(ExtendedActionMapper::toDto);

        // Trả về dạng PagedResult (custom wrapper)
        return new PagedResult<>(dtoPage);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"extended_actions_list"}, allEntries = true)
    public ExtendedActionDto createExtendedAction(ExtendedActionDto extendedActionDto) {

        var existed = extendedActionRepository.findByCodeIgnoreCaseAndStatusNot(extendedActionDto.getCode(), 0);
        if (existed.isPresent()) {
            throw new ConflictException("Đã tồn tại Extended Action với code: " + extendedActionDto.getCode());
        }

        existed = extendedActionRepository.findByNameIgnoreCaseAndStatusNot(extendedActionDto.getName(), 0);
        if (existed.isPresent()) {
            throw new ResourceNotFoundException("Đã tồn tại Extended Action với tên: " + extendedActionDto.getName());
        }
        ExtendedAction extendedAction = ExtendedActionMapper.toEntity(extendedActionDto);
        extendedAction.setCreatedDate(LocalDateTime.now());
        extendedAction.setLastUpdated(null);
        extendedAction.setStatus(1); // Mặc định là active
        ExtendedAction savedExtendedAction = extendedActionRepository.save(extendedAction);
        return ExtendedActionMapper.toDto(savedExtendedAction);
    }

    @Override
    @Transactional
    @CachePut(value = "extended_actions", key = "#id")
    @CacheEvict(value = {"extended_actions_list"}, allEntries = true)
    public ExtendedActionDto updateExtendedAction(UUID id, ExtendedActionDto extendedActionDto) {
        ExtendedAction existingExtendedAction = extendedActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy extended action với id: " + id));

        existingExtendedAction.setName(extendedActionDto.getName());
        existingExtendedAction.setCode(extendedActionDto.getCode());
        existingExtendedAction.setLastUpdated(LocalDateTime.now());
        existingExtendedAction.setStatus(extendedActionDto.getStatus());
        existingExtendedAction.setIcon(extendedActionDto.getIcon());
        existingExtendedAction.setRobotModelId(extendedActionDto.getRobotModelId());

        ExtendedAction updatedExtendedAction = extendedActionRepository.save(existingExtendedAction);
        return ExtendedActionMapper.toDto(updatedExtendedAction);
    }

    @Override
    @Transactional
    @CachePut(value = "extended_actions", key = "#id")
    @CacheEvict(value = {"extended_actions_list"}, allEntries = true)
    public ExtendedActionDto patchUpdateExtendedAction(UUID id, ExtendedActionDto extendedActionDto) {
        ExtendedAction existingExtendedAction = extendedActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy extended action với id: " + id));

        if (extendedActionDto.getName() != null) {
            existingExtendedAction.setName(extendedActionDto.getName());
        }

        if (extendedActionDto.getCode() != null) {
            existingExtendedAction.setCode(extendedActionDto.getCode());
        }

        if (extendedActionDto.getStatus() != null) {
            existingExtendedAction.setStatus(extendedActionDto.getStatus());
        }

        if (extendedActionDto.getIcon() != null) {
            existingExtendedAction.setIcon(extendedActionDto.getIcon());
        }

        if (extendedActionDto.getRobotModelId() != null) {
            existingExtendedAction.setRobotModelId(extendedActionDto.getRobotModelId());
        }

        existingExtendedAction.setLastUpdated(LocalDateTime.now());

        ExtendedAction updatedExtendedAction = extendedActionRepository.save(existingExtendedAction);
        return ExtendedActionMapper.toDto(updatedExtendedAction);

    }

    @Override
    @Transactional
    @CachePut(value = "extended_actions", key = "#id")
    @CacheEvict(value = {"extended_actions_list"}, allEntries = true)
    public ExtendedActionDto changeExtendedActionStatus(UUID id, Integer status) {
        ExtendedAction existingExtendedAction = extendedActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy extended action với id: " + id));
        existingExtendedAction.setStatus(status);
        existingExtendedAction.setLastUpdated(LocalDateTime.now());
        ExtendedAction updatedExtendedAction = extendedActionRepository.save(existingExtendedAction);
        return ExtendedActionMapper.toDto(updatedExtendedAction);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"extended_actions", "extended_actions_list"}, key = "#id", allEntries = true)
    public String deleteExtendedAction(UUID id) {
        ExtendedAction existingExtendedAction = extendedActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy extend action với id: " + id));

        existingExtendedAction.setStatus(0);
        existingExtendedAction.setLastUpdated(LocalDateTime.now());
        extendedActionRepository.save(existingExtendedAction);
        return "Extended Action deleted successfully";
    }
}
