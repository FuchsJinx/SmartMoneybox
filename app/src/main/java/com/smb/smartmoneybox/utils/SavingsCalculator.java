package com.smb.smartmoneybox.utils;

import com.smb.smartmoneybox.data.entities.Expense;

import java.util.List;

public class SavingsCalculator {

    public double calculateMonthlySavingsCapacity(double monthlyIncome, List<Expense> expenses) {
        double totalMonthlyExpenses = calculateTotalMonthlyExpenses(expenses);
        return monthlyIncome - totalMonthlyExpenses;
    }

    public double calculateTotalMonthlyExpenses(List<Expense> expenses) {
        double total = 0;
        for (Expense expense : expenses) {
            total += convertToMonthly(expense.getAmount(), expense.getType());
        }
        return total;
    }

    private double convertToMonthly(double amount, Expense.ExpenseType type) {
        switch (type) {
            case DAILY:
                return amount * 30; // Приблизительно 30 дней в месяце
            case WEEKLY:
                return amount * 4;  // Приблизительно 4 недели в месяце
            case MONTHLY:
                return amount;
            default:
                return amount;
        }
    }

    public double calculateRecommendedSavings(double monthlyIncome, double monthlyExpenses) {
        double available = monthlyIncome - monthlyExpenses;
        // Рекомендуем откладывать 70% от свободных средств
        return available * 0.7;
    }

    public double calculateSafeSavings(double monthlyIncome, double monthlyExpenses) {
        double available = monthlyIncome - monthlyExpenses;
        // Безопасный вариант - 50% от свободных средств
        return available * 0.5;
    }
}
