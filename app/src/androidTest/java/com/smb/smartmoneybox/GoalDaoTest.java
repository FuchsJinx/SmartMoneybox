package com.smb.smartmoneybox;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.smb.smartmoneybox.data.AppDatabase;
import com.smb.smartmoneybox.data.dao.GoalDao;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class GoalDaoTest {
    private AppDatabase database;
    private GoalDao goalDao;

    @Before
    public void setup() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase.class
        ).allowMainThreadQueries().build();
        goalDao = database.goalDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetGoal() {
        Goal goal = new Goal("Тест", 1000, Priority.HIGH, "2023-10-01");
        goalDao.insert(goal);

        Goal loaded = goalDao.getGoalByIdSync(goal.getId());
        assertNotNull(loaded);
        assertEquals("Тест", loaded.getName());
        assertEquals(1000, loaded.getTargetAmount(), 0.001);
    }

    @Test
    public void testGetAllGoals() {
        Goal goal1 = new Goal("Цель 1", 1000, Priority.HIGH, "2023-10-01");
        Goal goal2 = new Goal("Цель 2", 2000, Priority.MEDIUM, "2023-10-02");

        goalDao.insert(goal1);
        goalDao.insert(goal2);

        List<Goal> goals = goalDao.getAllGoalsSync();
        assertEquals(2, goals.size());
    }

    @Test
    public void testUpdateGoal() {
        Goal goal = new Goal("Тест", 1000, Priority.HIGH, "2023-10-01");
        goalDao.insert(goal);

        goal.setName("Обновленное название");
        goal.setTargetAmount(1500);
        goalDao.update(goal);

        Goal updated = goalDao.getGoalByIdSync(goal.getId());
        assertEquals("Обновленное название", updated.getName());
        assertEquals(1500, updated.getTargetAmount(), 0.001);
    }

    @Test
    public void testDeleteGoal() {
        Goal goal = new Goal("Тест", 1000, Priority.HIGH, "2023-10-01");
        goalDao.insert(goal);

        goalDao.delete(goal);

        List<Goal> goals = goalDao.getAllGoalsSync();
        assertTrue(goals.isEmpty());
    }

    @Test
    public void testGoalsOrderByDate() {
        Goal goal1 = new Goal("Цель 1", 1000, Priority.HIGH, "2023-10-01");
        Goal goal2 = new Goal("Цель 2", 2000, Priority.MEDIUM, "2023-10-02");
        Goal goal3 = new Goal("Цель 3", 3000, Priority.LOW, "2023-10-03");

        // Вставляем в обратном порядке для проверки сортировки
        goalDao.insert(goal3);
        goalDao.insert(goal2);
        goalDao.insert(goal1);

        List<Goal> goals = goalDao.getAllGoalsSync();
        assertEquals("Цель 1", goals.get(0).getName()); // Самая новая (последняя вставленная)
        assertEquals("Цель 3", goals.get(2).getName()); // Самая старая
    }
}