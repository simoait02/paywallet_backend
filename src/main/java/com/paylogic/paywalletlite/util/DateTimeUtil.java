package com.paylogic.paywalletlite.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {

    /**
     * Parse une date ISO 8601 en LocalDateTime.
     * Gère les formats avec et sans timezone (Z, +01:00, etc.).
     *
     * @param dateTimeStr la chaîne de date à parser
     * @return LocalDateTime parsé, ou la date/heure actuelle si la chaîne est null/vide
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            // Essayer avec Instant (format avec Z, ex: 2024-01-01T12:00:00Z)
            return LocalDateTime.ofInstant(
                    java.time.Instant.parse(dateTimeStr),
                    ZoneId.systemDefault()
            );
        } catch (DateTimeParseException e1) {
            try {
                // Essayer avec OffsetDateTime (format avec +01:00, ex: 2024-01-01T12:00:00+01:00)
                return OffsetDateTime.parse(dateTimeStr).toLocalDateTime();
            } catch (DateTimeParseException e2) {
                try {
                    // Fallback : format simple sans timezone (ex: 2024-01-01T12:00:00)
                    return LocalDateTime.parse(dateTimeStr);
                } catch (DateTimeParseException e3) {
                    // Dernier recours : retourner la date/heure actuelle
                    return LocalDateTime.now();
                }
            }
        }
    }
}