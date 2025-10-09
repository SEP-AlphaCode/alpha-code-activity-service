package com.alpha_code.alpha_code_activity_service.dto;

import com.alpha_code.alpha_code_activity_service.enums.ActionEnum;
import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SkillDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @NotBlank(message = "Name is required", groups = {OnCreate.class})
    private String name;

    @NotBlank(message = "Code is required", groups = {OnCreate.class})
    private String code;

    @NotNull(message = "Icon is required", groups = {OnCreate.class})
    private String icon;

    @NotNull(message = "Status is required", groups = {OnCreate.class})
    private Integer status;

    @NotNull(message = "Robot model id is required", groups = {OnCreate.class})
    private UUID robotModelId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String robotModelName;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return ActionEnum.fromCode(this.status);
    }
}
