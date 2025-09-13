package com.smb.smartmoneybox.utils;


import androidx.room.TypeConverter;

import com.smb.smartmoneybox.data.entities.Expense;

public class ExpenseTypeConverter {
    @TypeConverter
    public static Expense.ExpenseType fromString(String value) {
        return Expense.ExpenseType.valueOf(value);
    }

    @TypeConverter
    public static String expenseTypeToString(Expense.ExpenseType type) {
        return type.name();
    }
}