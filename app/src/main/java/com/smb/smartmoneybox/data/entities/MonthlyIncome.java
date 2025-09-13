package com.smb.smartmoneybox.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "monthly_income")
public class MonthlyIncome {
    @PrimaryKey
    @NonNull
    private String id = "current_income"; // Всегда один запись
    private double amount;
    private String updatedAt;

    public MonthlyIncome(double amount, String updatedAt) {
        this.amount = amount;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
