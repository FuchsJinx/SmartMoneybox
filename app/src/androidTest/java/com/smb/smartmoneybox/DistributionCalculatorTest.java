package com.smb.smartmoneybox;

import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;
import com.smb.smartmoneybox.utils.DistributionCalculator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DistributionCalculatorTest {
    private DistributionCalculator calculator;
    private List<Goal> testGoals;

    @Before
    public void setup() {
        calculator = new DistributionCalculator();
        createTestGoals();
    }

    private void createTestGoals() {
        Goal goal1 = new Goal("Высокий приоритет", 1000, Priority.HIGH, "2023-10-01");
        goal1.setCurrentAmount(200);

        Goal goal2 = new Goal("Средний приоритет", 2000, Priority.MEDIUM, "2023-10-02");
        goal2.setCurrentAmount(500);

        Goal goal3 = new Goal("Низкий приоритет", 1500, Priority.LOW, "2023-10-03");
        goal3.setCurrentAmount(1000);

        Goal goal4 = new Goal("Уже выполнена", 1000, Priority.HIGH, "2023-10-04");
        goal4.setCurrentAmount(1000);

        testGoals = Arrays.asList(goal1, goal2, goal3, goal4);
    }

    @Test
    public void testDistributionWithSufficientFunds() {
        double income = 3000;
        Map<String, Double> result = calculator.distributeFunds(income, testGoals);

        assertNotNull(result);
        assertEquals(3, result.size());

        Goal highPriority = testGoals.get(0);
        Goal mediumPriority = testGoals.get(1);
        Goal lowPriority = testGoals.get(2);

        assertEquals(800.0, result.get(highPriority.getId()), 0.001);
        assertEquals(1500.0, result.get(mediumPriority.getId()), 0.001);
        assertEquals(500.0, result.get(lowPriority.getId()), 0.001);
    }

    @Test
    public void testDistributionPriorityOrder() {
        Goal low = new Goal("Низкий", 1000, Priority.LOW, "2023-10-01");
        Goal high = new Goal("Высокий", 1000, Priority.HIGH, "2023-10-02");
        Goal medium = new Goal("Средний", 1000, Priority.MEDIUM, "2023-10-03");

        List<Goal> goals = Arrays.asList(low, high, medium);
        double income = 500;

        Map<String, Double> result = calculator.distributeFunds(income, goals);

        assertEquals(1, result.size());
        assertTrue(result.containsKey(high.getId()));
        assertEquals(500.0, result.get(high.getId()), 0.001);
    }

    @Test
    public void testEmptyGoalsList() {
        double income = 1000;
        Map<String, Double> result = calculator.distributeFunds(income, Arrays.asList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAllGoalsCompleted() {
        testGoals.forEach(goal -> goal.setCurrentAmount(goal.getTargetAmount()));

        double income = 1000;
        Map<String, Double> result = calculator.distributeFunds(income, testGoals);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}