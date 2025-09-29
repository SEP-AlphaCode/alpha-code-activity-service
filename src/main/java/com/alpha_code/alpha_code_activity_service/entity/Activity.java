package com.alpha_code.alpha_code_activity_service.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activity")
public class Activity {
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

    @NotNull
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @NotNull
    @Type(JsonType.class)
    @Column(name = "data", nullable = false, columnDefinition = "jsonb")
    private JsonNode data;

    @Size(max = 255)
    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    //Relationship
    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    private List<QrCode> qrCodes;

}