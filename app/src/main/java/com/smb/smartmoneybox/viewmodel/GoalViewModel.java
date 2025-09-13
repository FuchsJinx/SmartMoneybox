package com.smb.smartmoneybox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.repository.GoalRepository;

import java.util.List;

public class GoalViewModel extends AndroidViewModel {
    private GoalRepository repository;
    private LiveData<List<Goal>> allGoals;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public GoalViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public void insert(Goal goal) {
        if (isGoalValid(goal)) {
            repository.insert(goal);
        } else {
            errorMessage.setValue("Неверные данные цели");
        }
    }

    public void update(Goal goal) {
        if (isGoalValid(goal)) {
            repository.update(goal);
        } else {
            errorMessage.setValue("Неверные данные цели");
        }
    }

    public void delete(Goal goal) {
        repository.delete(goal);
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public LiveData<Goal> getGoalById(String goalId) {
        return repository.getGoalById(goalId);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public boolean isGoalValid(Goal goal) {
        return goal != null &&
                goal.getName() != null &&
                !goal.getName().trim().isEmpty() &&
                goal.getTargetAmount() > 0;
    }
}
