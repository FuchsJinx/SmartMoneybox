package com.smb.smartmoneybox.data.repository;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.AppDatabase;
import com.smb.smartmoneybox.data.dao.TransactionDao;
import com.smb.smartmoneybox.data.entities.Transaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private TransactionDao transactionDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
    }

    public void insert(Transaction transaction) {
        executor.execute(() -> transactionDao.insert(transaction));
    }

    public LiveData<List<Transaction>> getTransactionsByGoalId(String goalId) {
        return transactionDao.getTransactionsByGoalId(goalId);
    }

    public void deleteTransactionsByGoalId(String goalId) {
        executor.execute(() -> transactionDao.deleteTransactionsByGoalId(goalId));
    }
}
