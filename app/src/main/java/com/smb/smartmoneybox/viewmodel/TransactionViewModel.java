package com.smb.smartmoneybox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.entities.Transaction;
import com.smb.smartmoneybox.data.repository.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private TransactionRepository repository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public LiveData<List<Transaction>> getTransactionsByGoalId(String goalId) {
        return repository.getTransactionsByGoalId(goalId);
    }

    public void deleteTransactionsByGoalId(String goalId) {
        repository.deleteTransactionsByGoalId(goalId);
    }
}
