package ru.yandex.practicum.collector.dto.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.collector.dto.hub.device.DeviceAddedEventDto;
import ru.yandex.practicum.collector.dto.hub.device.DeviceRemovedEventDto;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioAddedEventDto;
import ru.yandex.practicum.collector.dto.hub.scenario.ScenarioRemovedEventDto;
import ru.yandex.practicum.collector.enums.HubEventType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEventDto.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEventDto.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEventDto.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEventDto.class, name = "SCENARIO_REMOVED")
})
@Getter
@Setter
public abstract class HubEventDto {
    @NotBlank
    private String hubId;
    private Instant timestamp = Instant.now();

    @NotNull
    public abstract HubEventType getType();
}