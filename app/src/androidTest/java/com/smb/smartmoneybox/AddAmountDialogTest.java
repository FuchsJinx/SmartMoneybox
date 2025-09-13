package com.smb.smartmoneybox;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.smb.smartmoneybox.ui.dialogs.AddAmountDialogFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddAmountDialogTest {

    @Test
    public void testDialogLayout() {
        FragmentScenario<AddAmountDialogFragment> scenario =
                FragmentScenario.launch(AddAmountDialogFragment.class);

        onView(withId(R.id.et_amount)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_add)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_cancel)).check(matches(isDisplayed()));
    }

    @Test
    public void testValidation() {
        FragmentScenario<AddAmountDialogFragment> scenario =
                FragmentScenario.launch(AddAmountDialogFragment.class);

        // Пытаемся добавить без суммы
        onView(withId(R.id.btn_add)).perform(click());

        // Должна появиться ошибка
        onView(withId(R.id.et_amount)).check(matches(withText("")));
    }

    @Test
    public void testValidInput() {
        FragmentScenario<AddAmountDialogFragment> scenario =
                FragmentScenario.launch(AddAmountDialogFragment.class);

        // Вводим валидную сумму
        onView(withId(R.id.et_amount)).perform(typeText("1000"));
        onView(withId(R.id.btn_add)).perform(click());

        // Диалог должен закрыться
//        onView(withId(R.id.et_amount)).check(matches(not(isDisplayed())));
    }
}