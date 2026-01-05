package ru.yandex.practicum.collector.dto.hub.device;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.enums.DeviceType;
import ru.yandex.practicum.collector.enums.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEventDto extends HubEventDto {

    @NotNull
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
