package com.alpha_code.alpha_code_activity_service.mapper;

import com.alpha_code.alpha_code_activity_service.dto.ExpressionDto;
import com.alpha_code.alpha_code_activity_service.entity.Expression;

public class ExpressionMapper {
    public static ExpressionDto toDto(Expression expression){
        if (expression == null) {
            return null;
        }

        ExpressionDto expressionDto = new ExpressionDto();
        expressionDto.setId(expression.getId());
        expressionDto.setName(expression.getName());
        expressionDto.setCode(expression.getCode());
        expressionDto.setImageUrl(expression.getImageUrl());
        expressionDto.setCreatedDate(expression.getCreatedDate());
        expressionDto.setLastUpdated(expression.getLastUpdated());
        expressionDto.setStatus(expression.getStatus());
        expressionDto.setRobotModelId(expression.getRobotModelId());
        return expressionDto;
    }

    public static Expression toEntity(ExpressionDto dto){
        if (dto == null) {
            return null;
        }

        Expression expression = new Expression();
        expression.setId(dto.getId());
        expression.setName(dto.getName());
        expression.setCode(dto.getCode());
        expression.setImageUrl(dto.getImageUrl());
        expression.setCreatedDate(dto.getCreatedDate());
        expression.setLastUpdated(dto.getLastUpdated());
        expression.setStatus(dto.getStatus());
        expression.setRobotModelId(dto.getRobotModelId());
        return expression;
    }
}
