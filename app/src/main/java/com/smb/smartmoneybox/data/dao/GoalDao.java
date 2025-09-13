package com.smb.smartmoneybox.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smb.smartmoneybox.data.entities.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    List<Goal> getAllGoalsSync(); // Синхронная версия для тестов

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    LiveData<List<Goal>> getAllGoals(); // Асинхронная версия для UI

    @Query("SELECT * FROM goals WHERE id = :goalId")
    LiveData<Goal> getGoalById(String goalId);

    // Синхронные методы для тестирования
    @Query("SELECT * FROM goals WHERE id = :goalId")
    Goal getGoalByIdSync(String goalId);
}
