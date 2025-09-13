package com.smb.smartmoneybox.utils;

import androidx.room.TypeConverter;

import com.smb.smartmoneybox.data.entities.Priority;

public class Converters {
    @TypeConverter
    public static Priority fromString(String value) {
        return Priority.valueOf(value);
    }

    @TypeConverter
    public static String priorityToString(Priority priority) {
        return priority.name();
    }
}
