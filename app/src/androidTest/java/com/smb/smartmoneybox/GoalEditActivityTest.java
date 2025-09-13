package com.smb.smartmoneybox;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.ui.activities.GoalEditActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GoalEditActivityTest {

    @Test
    public void testGoalEditActivityLayout() {
        try (ActivityScenario<GoalEditActivity> scenario = ActivityScenario.launch(GoalEditActivity.class)) {
            Thread.sleep(500);

            Espresso.onView(ViewMatchers.withId(R.id.et_goal_name))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.et_target_amount))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGoalCreationValidation() {
        try (ActivityScenario<GoalEditActivity> scenario = ActivityScenario.launch(GoalEditActivity.class)) {
            Thread.sleep(500);

            // Пытаемся сохранить без данных
            Espresso.onView(ViewMatchers.withId(R.id.btn_save))
                    .perform(ViewActions.click());

            // Проверяем ошибки валидации
            Espresso.onView(ViewMatchers.withId(R.id.et_goal_name))
                    .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Введите название цели")));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}