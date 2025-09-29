package com.alpha_code.alpha_code_activity_service.service;


import com.alpha_code.alpha_code_activity_service.dto.OsmoCardDto;
import com.alpha_code.alpha_code_activity_service.dto.PagedResult;

import java.util.UUID;

public interface OsmoCardService {
    PagedResult<OsmoCardDto> getAll(int page, int size, Integer status);

    OsmoCardDto getById(UUID id);

    OsmoCardDto create(OsmoCardDto dto);

    OsmoCardDto update(UUID id, OsmoCardDto dto);

    OsmoCardDto patchUpdate(UUID id, OsmoCardDto dto);

    String delete(UUID id);

    OsmoCardDto changeStatus(UUID id, Integer status);
}
