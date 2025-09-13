package com.smb.smartmoneybox.utils;

import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DistributionCalculator {

    public Map<String, Double> distributeFunds(double income, List<Goal> goals) {
        Map<String, Double> result = new HashMap<>();
        double remaining = income;

        // Фильтрация и сортировка по приоритету
        List<Goal> sortedGoals = goals.stream()
                .filter(goal -> goal.getCurrentAmount() < goal.getTargetAmount())
                .sorted((g1, g2) -> Integer.compare(g2.getPriority().getValue(), g1.getPriority().getValue()))
                .collect(Collectors.toList());

        for (Goal goal : sortedGoals) {
            if (remaining <= 0) break;

            double needed = goal.getTargetAmount() - goal.getCurrentAmount();
            double toAdd = Math.min(needed, remaining);

            result.put(goal.getId(), toAdd);
            remaining -= toAdd;
        }

        return result;
    }
}