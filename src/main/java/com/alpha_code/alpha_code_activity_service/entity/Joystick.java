package com.alpha_code.alpha_code_activity_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "joystick",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "robot_id", "button_code"})
        }
)
public class Joystick {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "account_id", nullable = false, columnDefinition = "uuid")
    private UUID accountId;

    @NotNull
    @Column(name = "robot_id", nullable = false, columnDefinition = "uuid")
    private UUID robotId;

    @NotNull
    @Column(name = "button_code", nullable = false, length = 50)
    private String buttonCode;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    // ID của từng loại hành động — CHO PHÉP NULL
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

    // Liên kết — dùng LAZY để tránh load thừa
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Action action;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expression_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Expression expression;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dance_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Dance dance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Skill skill;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extended_action_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ExtendedAction extendedAction;
}
