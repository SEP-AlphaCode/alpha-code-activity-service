package com.alpha_code.alpha_code_activity_service.dto;

import com.alpha_code.alpha_code_activity_service.enums.ActionEnum;
import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Name is required", groups = {OnCreate.class})
    private String name;

    @NotNull(message = "Code is required", groups = {OnCreate.class})
    private String code;

    @NotNull(message = "Image url is required", groups = {OnCreate.class})
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    private Integer status;

    private UUID robotModelId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String robotModelName;


    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return ActionEnum.fromCode(this.status);
    }
}
