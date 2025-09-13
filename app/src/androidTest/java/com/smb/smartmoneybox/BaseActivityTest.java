package com.smb.smartmoneybox;

import androidx.test.core.app.ActivityScenario;

import org.junit.After;

public abstract class BaseActivityTest {

    protected ActivityScenario<?> scenario;

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
