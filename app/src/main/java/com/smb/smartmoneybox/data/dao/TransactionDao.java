package com.smb.smartmoneybox.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.smb.smartmoneybox.data.entities.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE goalId = :goalId ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsByGoalId(String goalId);

    @Query("DELETE FROM transactions WHERE goalId = :goalId")
    void deleteTransactionsByGoalId(String goalId);
}
