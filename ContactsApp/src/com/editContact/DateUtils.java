package com.editContact;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*
Contacts App : UC-10 Filter Contacts
This class helps with simple date parsing.
It does the following things:
    - Parses strings like "2026-02-28" to a LocalDate
    - Uses a fixed format: yyyy-MM-dd
    - Returns null if the text is empty or invalid
    - Keeps things small and beginner-friendly

@author Developer
@version 10.0
*/

public class DateUtils {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDateOrNull(String text) {
        if (text == null) return null;
        String t = text.trim();
        if (t.isEmpty()) return null;
        try {
            return LocalDate.parse(t, FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
