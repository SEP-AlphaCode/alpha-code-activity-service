package com.alpha_code.alpha_code_activity_service.dto;

import com.alpha_code.alpha_code_activity_service.enums.ActionEnum;
import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
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
public class DanceDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Name is required", groups = {OnCreate.class})
    private String name;

    @NotNull(message = "Duration is required", groups = {OnCreate.class})
    private Double duration;

    private String description;

    @NotNull(message = "Code is required", groups = {OnCreate.class})
    private String code;

    private Integer status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @NotNull(message = "Icon is required", groups = {OnCreate.class})
    private String icon;

    @NotNull(message = "Robot model id is required", groups = {OnCreate.class})
    private UUID robotModelId;

    @JsonProperty(value = "statusText", access = JsonProperty.Access.READ_ONLY)
    public String getStatusText() {
        return ActionEnum.fromCode(this.status);
    }
}
