package com.alpha_code.alpha_code_activity_service.dto;

import com.alpha_code.alpha_code_activity_service.validation.OnCreate;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class JoystickDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotNull(message = "Account id là bắt buộc", groups = {OnCreate.class})
    private UUID accountId;

    @NotNull(message = "Robot id là bắt buộc", groups = {OnCreate.class})
    private UUID robotId;

    @NotNull(message = "Button là bắt buộc", groups = {OnCreate.class})
    private String buttonCode;

    private Integer status;

    @NotNull(message = "Type là bắt buộc", groups = {OnCreate.class})
    private String type;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime lastUpdated;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    private UUID expressionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String expressionName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String expressionCode;

    private UUID actionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String actionName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String actionCode;

    private UUID danceId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String danceName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String danceCode;

    private UUID skillId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String skillName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String skillCode;

    private UUID extendedActionId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String extendedActionName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String extendedActionCode;
}
