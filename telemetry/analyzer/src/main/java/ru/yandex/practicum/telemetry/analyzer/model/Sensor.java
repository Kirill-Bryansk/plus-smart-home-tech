package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensors")
@Getter @Setter @NoArgsConstructor
public class Sensor {
    @Id
    private String id;

    @Column(name = "hub_id")
    private String hubId;
}