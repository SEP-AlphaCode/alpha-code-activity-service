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
@Table(name = "osmo_card")
public class OsmoCard {
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
    @Column(name = "color", nullable = false)
    private String color;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "action_id", insertable = false, updatable = false)
    private UUID actionId;

    @Column(name = "expression_id", insertable = false, updatable = false)
    private UUID expressionId;

    @Column(name = "dance_id", insertable = false, updatable = false)
    private UUID danceId;

    @Column(name = "skill_id", insertable = false, updatable = false)
    private UUID skillId;

    @Column(name = "extended_action_id", insertable = false, updatable = false)
    private UUID extendedActionId;

    //Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", insertable = false, updatable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expression_id", insertable = false, updatable = false)
    private Expression expression;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dance_id", insertable = false, updatable = false)
    private Dance dance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extended_action_id", insertable = false, updatable = false)
    private ExtendedAction extendedAction;
}