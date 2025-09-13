package com.smb.smartmoneybox.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.smb.smartmoneybox.utils.Converters;

import java.util.UUID;

@Entity(tableName = "goals")
@TypeConverters(Converters.class)
public class Goal {
    @PrimaryKey
    @NonNull
    private String id = UUID.randomUUID().toString();
    private String name;
    private double targetAmount;
    private double currentAmount;
    private Priority priority;
    private String createdAt;

    public Goal(String name, double targetAmount, Priority priority, String createdAt) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.priority = priority;
        this.createdAt = createdAt;
        this.currentAmount = 0;
    }

    // Геттеры и сеттеры
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
