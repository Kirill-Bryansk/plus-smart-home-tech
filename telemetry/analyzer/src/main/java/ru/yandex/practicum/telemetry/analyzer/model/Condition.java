package ru.yandex.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.enums.ConditionType;

@Entity
@Table(name = "conditions")
@Getter @Setter @NoArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private ConditionOperation operation;

    @Column(name = "value")
    private Integer value; // Только Integer по ТЗ
}