package com.smb.smartmoneybox;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GoalViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private GoalViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new GoalViewModel(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testGoalValidation() {
        Goal validGoal = new Goal("Valid Goal", 1000, Priority.HIGH, "2023-10-01");
        assertTrue(viewModel.isGoalValid(validGoal));

        Goal invalidName = new Goal("", 1000, Priority.HIGH, "2023-10-01");
        assertFalse(viewModel.isGoalValid(invalidName));

        Goal invalidAmount = new Goal("Invalid Amount", 0, Priority.HIGH, "2023-10-01");
        assertFalse(viewModel.isGoalValid(invalidAmount));
    }

    @Test
    public void testErrorMessageLiveData() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(String error) {
                assertNotNull(error);
                assertTrue(error.contains("Неверные данные цели"));
            }
        };

        viewModel.getErrorMessage().observeForever(observer);

        Goal invalidGoal = new Goal("", 1000, Priority.HIGH, "2023-10-01");
        viewModel.insert(invalidGoal);
    }

    @Test
    public void testGoalsLiveData() {
        Observer<List<Goal>> observer = new Observer<List<Goal>>() {
            @Override
            public void onChanged(List<Goal> goals) {
                assertNotNull(goals);
                assertTrue(goals.isEmpty()); // Изначально список должен быть пустым
            }
        };

        viewModel.getAllGoals().observeForever(observer);
    }
}