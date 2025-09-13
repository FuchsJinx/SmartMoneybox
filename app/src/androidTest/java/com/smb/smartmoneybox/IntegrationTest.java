package com.smb.smartmoneybox;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smb.smartmoneybox.ui.activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IntegrationTest {

    @Test
    public void testAppLaunch() {
        // Простой тест запуска приложения
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(2000); // Даем время на полную загрузку

            // Проверяем, что приложение запустилось
            Espresso.onView(ViewMatchers.withId(R.id.toolbar))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasicNavigation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Thread.sleep(1000);

            // Проверяем наличие основных элементов
            Espresso.onView(ViewMatchers.withId(R.id.fab_add_goal))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}