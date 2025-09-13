package com.smb.smartmoneybox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.utils.DistributionCalculator;

import java.util.List;
import java.util.Map;

public class DistributionViewModel extends AndroidViewModel {
    private MutableLiveData<Map<String, Double>> distributionResult = new MutableLiveData<>();
    private MutableLiveData<String> distributionError = new MutableLiveData<>();
    private DistributionCalculator calculator = new DistributionCalculator();

    public DistributionViewModel(@NonNull Application application) {
        super(application);
    }

    public void calculateDistribution(double income, List<Goal> goals) {
        if (income <= 0) {
            distributionError.setValue("Сумма дохода должна быть положительной");
            return;
        }

        if (goals == null || goals.isEmpty()) {
            distributionError.setValue("Нет целей для распределения");
            return;
        }

        try {
            Map<String, Double> result = calculator.distributeFunds(income, goals);
            distributionResult.setValue(result);
        } catch (Exception e) {
            distributionError.setValue("Ошибка при расчете распределения: " + e.getMessage());
        }
    }

    public LiveData<Map<String, Double>> getDistributionResult() {
        return distributionResult;
    }

    public LiveData<String> getDistributionError() {
        return distributionError;
    }

    public void resetDistribution() {
        distributionResult.setValue(null);
        distributionError.setValue(null);
    }
}
