package com.smb.smartmoneybox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smb.smartmoneybox.data.entities.Expense;
import com.smb.smartmoneybox.data.repository.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository repository;
    private LiveData<List<Expense>> allExpenses;
    private LiveData<Double> monthlyExpensesTotal;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        allExpenses = repository.getAllExpenses();
        monthlyExpensesTotal = repository.getMonthlyExpensesTotal();
    }

    public void insert(Expense expense) {
        if (isExpenseValid(expense)) {
            repository.insert(expense);
        } else {
            errorMessage.setValue("Неверные данные траты");
        }
    }

    public void update(Expense expense) {
        if (isExpenseValid(expense)) {
            repository.update(expense);
        } else {
            errorMessage.setValue("Неверные данные траты");
        }
    }

    public void delete(Expense expense) {
        repository.delete(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<Expense> getExpenseById(String expenseId) {
        return repository.getExpenseById(expenseId);
    }

    public LiveData<Double> getMonthlyExpensesTotal() {
        return monthlyExpensesTotal;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private boolean isExpenseValid(Expense expense) {
        return expense != null &&
                expense.getName() != null &&
                !expense.getName().trim().isEmpty() &&
                expense.getAmount() > 0;
    }
}
