package ru.yandex.practicum.collector.dto.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.enums.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEventDto extends HubEventDto {


    @NotBlank
    @Size(min = 3)
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
