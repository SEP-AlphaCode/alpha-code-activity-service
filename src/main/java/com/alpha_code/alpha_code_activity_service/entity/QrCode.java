package com.alpha_code.alpha_code_activity_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "qr_code")
public class QrCode {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "qr_code", nullable = false)
    private String qrCode;

    @Size(max = 100)
    @Column(name = "color", length = 100)
    private String color;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Size(max = 255)
    @NotNull
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "activity_id")
    private UUID activityId;

    // Các ID chỉ để đọc (không ghi đè, không insert/update)
    @Column(name = "action_id")
    private UUID actionId;

    @Column(name = "expression_id")
    private UUID expressionId;

    @Column(name = "dance_id")
    private UUID danceId;

    @Column(name = "skill_id")
    private UUID skillId;

    @Column(name = "extended_action_id")
    private UUID extendedActionId;

    //Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    private Activity activity;

}