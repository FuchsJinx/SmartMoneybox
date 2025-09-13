package com.smb.smartmoneybox.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.AppDatabase;
import com.smb.smartmoneybox.data.dao.ExpenseDao;
import com.smb.smartmoneybox.data.entities.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private ExpenseDao expenseDao;
    private LiveData<List<Expense>> allExpenses;
    private LiveData<Double> monthlyExpensesTotal;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExpenseRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
        monthlyExpensesTotal = expenseDao.getMonthlyExpensesTotal();
    }

    public void insert(Expense expense) {
        executor.execute(() -> expenseDao.insert(expense));
    }

    public void update(Expense expense) {
        executor.execute(() -> expenseDao.update(expense));
    }

    public void delete(Expense expense) {
        executor.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<Expense> getExpenseById(String expenseId) {
        return expenseDao.getExpenseById(expenseId);
    }

    public LiveData<Double> getMonthlyExpensesTotal() {
        return monthlyExpensesTotal;
    }
}