package com.smb.smartmoneybox.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.smb.smartmoneybox.data.entities.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    LiveData<Expense> getExpenseById(String expenseId);

    @Query("SELECT SUM(amount * CASE type WHEN 'DAILY' THEN 30 WHEN 'WEEKLY' THEN 4 WHEN 'MONTHLY' THEN 1 ELSE 0 END) FROM expenses")
    LiveData<Double> getMonthlyExpensesTotal();
}