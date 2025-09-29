package com.alpha_code.alpha_code_activity_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "action")
public class Action {
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

    @Column(name = "description", columnDefinition = "text")
    @Lob
    private String description;

    @Size(max = 100)
    @NotNull
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @NotNull
    @Column(name = "can_interrupt", nullable = false)
    private Boolean canInterrupt = false;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "robot_model_id", nullable = false)
    private UUID robotModelId;

    //Relationship
    @OneToMany(mappedBy = "action", fetch = FetchType.LAZY)
    private List<OsmoCard> osmoCards;

}