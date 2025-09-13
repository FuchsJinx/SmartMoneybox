package com.smb.smartmoneybox.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey
    @NonNull
    private String id = UUID.randomUUID().toString();
    private String name;
    private double amount;
    private ExpenseType type; // DAILY, WEEKLY, MONTHLY
    private String createdAt;

    public enum ExpenseType {
        DAILY, WEEKLY, MONTHLY
    }

    public Expense(String name, double amount, ExpenseType type, String createdAt) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public ExpenseType getType() { return type; }
    public void setType(ExpenseType type) { this.type = type; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
