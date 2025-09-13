package com.smb.smartmoneybox;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.ui.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Test
    public void testMainActivityLayout() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            // Даем время для инициализации
            Thread.sleep(1000);

            // Проверяем основные элементы интерфейса
            Espresso.onView(ViewMatchers.withId(R.id.toolbar))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.rv_goals))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            Espresso.onView(ViewMatchers.withId(R.id.fab_add_goal))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddGoalButtonClick() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(1000);

            // Клик по FAB
            Espresso.onView(ViewMatchers.withId(R.id.fab_add_goal))
                    .perform(ViewActions.click());

            // Проверяем, что открылась активность редактирования
            Espresso.onView(ViewMatchers.withText("Сохранить"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}