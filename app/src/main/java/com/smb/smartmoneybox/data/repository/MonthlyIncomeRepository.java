package com.smb.smartmoneybox.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.AppDatabase;
import com.smb.smartmoneybox.data.dao.MonthlyIncomeDao;
import com.smb.smartmoneybox.data.entities.MonthlyIncome;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonthlyIncomeRepository {
    private MonthlyIncomeDao monthlyIncomeDao;
    private LiveData<MonthlyIncome> currentIncome;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public MonthlyIncomeRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        monthlyIncomeDao = database.monthlyIncomeDao();
        currentIncome = monthlyIncomeDao.getCurrentIncome();
    }

    public void insertOrUpdate(double amount) {
        executor.execute(() -> {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            MonthlyIncome income = new MonthlyIncome(amount, currentDate);
            monthlyIncomeDao.insert(income);
        });
    }

    public LiveData<MonthlyIncome> getCurrentIncome() {
        return currentIncome;
    }
}