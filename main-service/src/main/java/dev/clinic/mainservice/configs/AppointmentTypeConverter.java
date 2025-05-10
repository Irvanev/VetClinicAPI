package dev.clinic.mainservice.configs;

import dev.clinic.mainservice.models.enums.AppointmentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTypeConverter implements Converter<String, AppointmentType> {
    @Override
    public AppointmentType convert(String source) {
        return AppointmentType.fromName(source);
    }
}
