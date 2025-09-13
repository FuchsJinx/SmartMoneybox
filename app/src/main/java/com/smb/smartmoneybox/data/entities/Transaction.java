package com.smb.smartmoneybox.data.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(
        tableName = "transactions",
        foreignKeys = @ForeignKey(
                entity = Goal.class,
                parentColumns = "id",
                childColumns = "goalId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Transaction {
    @PrimaryKey
    @NonNull
    private String id = UUID.randomUUID().toString();
    private String goalId;
    private double amount;
    private String date;

    public Transaction(String goalId, double amount, String date) {
        this.goalId = goalId;
        this.amount = amount;
        this.date = date;
    }

    // Геттеры и сеттеры
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}