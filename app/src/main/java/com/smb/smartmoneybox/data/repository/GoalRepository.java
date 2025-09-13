package com.smb.smartmoneybox.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.AppDatabase;
import com.smb.smartmoneybox.data.dao.GoalDao;
import com.smb.smartmoneybox.data.entities.Goal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GoalRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        goalDao = database.goalDao();
        allGoals = goalDao.getAllGoals();
    }

    public void insert(Goal goal) {
        executor.execute(() -> goalDao.insert(goal));
    }

    public void update(Goal goal) {
        executor.execute(() -> goalDao.update(goal));
    }

    public void delete(Goal goal) {
        executor.execute(() -> goalDao.delete(goal));
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public LiveData<Goal> getGoalById(String goalId) {
        return goalDao.getGoalById(goalId);
    }
}