package com.agustincrespo.u3.jpa.basico.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true) // Convierte para que no de erorr -> Sqlite
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

	//.format puede dar error |-> controlar ex
    // Formato compatible con SQLite (yyyy-MM-dd HH:mm:ss)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? attribute.format(FORMATTER) : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalDateTime.parse(dbData, FORMATTER) : null;
    }
}
