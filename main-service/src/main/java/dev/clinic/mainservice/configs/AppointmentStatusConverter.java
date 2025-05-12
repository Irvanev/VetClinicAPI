package dev.clinic.mainservice.configs;

import dev.clinic.mainservice.models.enums.AppointmentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppointmentStatusConverter implements Converter<String, AppointmentStatus> {

    @Override
    public AppointmentStatus convert(String source) {
        return AppointmentStatus.fromName(source);
    }
}
