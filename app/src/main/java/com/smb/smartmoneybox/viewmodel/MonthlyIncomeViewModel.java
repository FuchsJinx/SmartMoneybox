package com.smb.smartmoneybox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.smb.smartmoneybox.data.entities.MonthlyIncome;
import com.smb.smartmoneybox.data.repository.MonthlyIncomeRepository;
import androidx.lifecycle.MutableLiveData;

public class MonthlyIncomeViewModel extends AndroidViewModel {
    private MonthlyIncomeRepository repository;
    private LiveData<MonthlyIncome> currentIncome;
    private MutableLiveData<Double> currentIncomeValue = new MutableLiveData<>(0.0);

    public MonthlyIncomeViewModel(@NonNull Application application) {
        super(application);
        repository = new MonthlyIncomeRepository(application);
        currentIncome = repository.getCurrentIncome();
    }

    public void setIncome(double amount) {
        repository.insertOrUpdate(amount);
    }

    public LiveData<MonthlyIncome> getCurrentIncome() {
        return currentIncome;
    }

    public LiveData<Double> getCurrentIncomeValue() {
        return currentIncomeValue;
    }

    public void updateCurrentIncomeValue(double income) {
        currentIncomeValue.setValue(income);
    }
}
