package com.alpha_code.alpha_code_activity_service.service.impl;

import com.alpha_code.alpha_code_activity_service.dto.OsmoCardDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;
import com.alpha_code.alpha_code_activity_service.entity.OsmoCard;
import com.alpha_code.alpha_code_activity_service.exception.ResourceNotFoundException;
import com.alpha_code.alpha_code_activity_service.mapper.OsmoCardMapper;
import com.alpha_code.alpha_code_activity_service.repository.OsmoCardRepository;
import com.alpha_code.alpha_code_activity_service.service.OsmoCardService;
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
public class OsmoCardServiceImpl implements OsmoCardService {

    private final OsmoCardRepository repository;

    @Override
    @Cacheable(value = "osmo_cards_list", key = "{#page, #size, #status}")
    public PagedResult<OsmoCardDto> getAll(int page, int size, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OsmoCard> pageResult;

        if (status != null) {
            pageResult = repository.findAllByStatus(status, pageable);
        } else {
            pageResult = repository.findAll(pageable);
        }
        return new PagedResult<>(pageResult.map(OsmoCardMapper::toDto));
    }

    @Override
    @Cacheable(value = "osmo_cards", key = "#id")
    public OsmoCardDto getById(UUID id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OsmoCard not found"));
        return OsmoCardMapper.toDto(entity);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"osmo_cards_list", "osmo_cards"}, allEntries = true)
    public OsmoCardDto create(OsmoCardDto dto) {
        var entity = OsmoCardMapper.toEntity(dto);
        if (entity.getActionId() == null && entity.getDanceId() == null && entity.getExpressionId() == null) {
            throw new IllegalArgumentException("Osmo Card must have at least one of Action ID, Dance ID, or Expression ID set");
        } else if (entity.getActionId() != null && entity.getDanceId() != null && entity.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have all Action ID, Dance ID, and Expression ID set at the same time");
        } else if (entity.getActionId() != null && entity.getDanceId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Dance ID set at the same time");
        } else if (entity.getActionId() != null && entity.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Expression ID set at the same time");
        } else if (entity.getDanceId() != null && entity.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Dance ID and Expression ID set at the same time");
        }

        entity.setColor(dto.getColor());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setLastUpdated(LocalDateTime.now());
        entity.setCreatedDate(LocalDateTime.now());
        if (entity.getActionId() != null) {
            entity.setActionId(entity.getActionId());
        } else if (entity.getDanceId() != null) {
            entity.setDanceId(entity.getDanceId());
        } else if (entity.getExpressionId() != null) {
            entity.setExpressionId(entity.getExpressionId());
        }


        var saved = repository.save(entity);
        return OsmoCardMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"osmo_cards_list"}, allEntries = true)
    @CachePut(value = "osmo_cards", key = "#id")
    public OsmoCardDto update(UUID id, OsmoCardDto dto) {
        if (dto.getActionId() == null && dto.getDanceId() == null && dto.getExpressionId() == null) {
            throw new IllegalArgumentException("Osmo Card must have at least one of Action ID, Dance ID, or Expression ID set");
        } else if (dto.getActionId() != null && dto.getDanceId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have all Action ID, Dance ID, and Expression ID set at the same time");
        } else if (dto.getActionId() != null && dto.getDanceId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Dance ID set at the same time");
        } else if (dto.getActionId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Expression ID set at the same time");
        } else if (dto.getDanceId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Dance ID and Expression ID set at the same time");
        }

        var existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OsmoCard not found"));

        existing.setName(dto.getName());
        existing.setColor(dto.getColor());
        existing.setStatus(dto.getStatus());
        existing.setLastUpdated(LocalDateTime.now());
        existing.setExpressionId(dto.getExpressionId());
        existing.setActionId(dto.getActionId());
        existing.setDanceId(dto.getDanceId());


        var updated = repository.save(existing);
        return OsmoCardMapper.toDto(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"osmo_cards_list"}, allEntries = true)
    @CachePut(value = "osmo_cards", key = "#id")
    public OsmoCardDto patchUpdate(UUID id, OsmoCardDto dto) {
        if (dto.getActionId() != null && dto.getDanceId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have all Action ID, Dance ID, and Expression ID set at the same time");
        } else if (dto.getActionId() != null && dto.getDanceId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Dance ID set at the same time");
        } else if (dto.getActionId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Action ID and Expression ID set at the same time");
        } else if (dto.getDanceId() != null && dto.getExpressionId() != null) {
            throw new IllegalArgumentException("Osmo Card cannot have both Dance ID and Expression ID set at the same time");
        }

        var existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OsmoCard not found"));

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getColor() != null) {
            existing.setColor(dto.getColor());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getExpressionId() != null) {
            existing.setExpressionId(dto.getExpressionId());
        }
        if (dto.getActionId() != null) {
            existing.setActionId(dto.getActionId());
        }
        if (dto.getDanceId() != null) {
            existing.setDanceId(dto.getDanceId());
        }
        existing.setLastUpdated(LocalDateTime.now());

        var updated = repository.save(existing);
        return OsmoCardMapper.toDto(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"osmo_cards", "osmo_cards_list"}, key = "#id", allEntries = true)
    public String delete(UUID id) {
        try {
            var existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("OsmoCard not found"));

            existing.setStatus(0);
            repository.save(existing);
            return "Deleted OsmoCard with ID: " + id;
        } catch (Exception e) {
            throw new RuntimeException("Error deleting OsmoCard", e);
        }

    }

    @Override
    @Transactional
    @CacheEvict(value = { "osmo_cards_list"},  allEntries = true)
    @CachePut(value = "osmo_cards", key = "#id")
    public OsmoCardDto changeStatus(UUID id, Integer status) {
        OsmoCard entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OsmoCard not found"));

        entity.setStatus(status);
        entity.setLastUpdated(LocalDateTime.now());

        OsmoCard updated = repository.save(entity);
        return OsmoCardMapper.toDto(updated);
    }
}
