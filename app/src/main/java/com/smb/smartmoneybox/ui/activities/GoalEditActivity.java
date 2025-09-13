package com.smb.smartmoneybox.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.databinding.ActivityGoalEditBinding;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class GoalEditActivity extends AppCompatActivity {

    private ActivityGoalEditBinding binding;
    private GoalViewModel goalViewModel;
    private String existingGoalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupListeners();
        loadExistingGoal();
    }

    private void setupViewModel() {
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        goalViewModel = new ViewModelProvider(this, factory).get(GoalViewModel.class);
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> saveGoal());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void loadExistingGoal() {
        existingGoalId = getIntent().getStringExtra("goal_id");
        if (existingGoalId != null) {
            goalViewModel.getGoalById(existingGoalId).observe(this, goal -> {
                if (goal != null) {
                    binding.etGoalName.setText(goal.getName());
                    binding.etTargetAmount.setText(String.valueOf(goal.getTargetAmount()));

                    switch (goal.getPriority()) {
                        case HIGH:
                            binding.rbHigh.setChecked(true);
                            break;
                        case MEDIUM:
                            binding.rbMedium.setChecked(true);
                            break;
                        case LOW:
                            binding.rbLow.setChecked(true);
                            break;
                    }
                }
            });
        }
    }

    private void saveGoal() {
        String name = binding.etGoalName.getText().toString().trim();
        String amountText = binding.etTargetAmount.getText().toString().trim();

        // Валидация
        boolean hasError = false;

        if (name.isEmpty()) {
            binding.etGoalName.setError("Введите название цели");
            hasError = true;
        } else {
            binding.etGoalName.setError(null);
        }

        if (amountText.isEmpty()) {
            binding.etTargetAmount.setError("Введите целевую сумму");
            hasError = true;
        } else {
            binding.etTargetAmount.setError(null);
        }

        if (hasError) {
            return;
        }

        try {
            double targetAmount = Double.parseDouble(amountText);
            if (targetAmount <= 0) {
                binding.etTargetAmount.setError("Сумма должна быть положительной");
                return;
            }

            Priority priority = getSelectedPriority();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Goal goal;
            if (existingGoalId != null) {
                // Редактирование существующей цели
                goalViewModel.getGoalById(existingGoalId).observe(this, existingGoal -> {
                    if (existingGoal != null) {
                        existingGoal.setName(name);
                        existingGoal.setTargetAmount(targetAmount);
                        existingGoal.setPriority(priority);
                        goalViewModel.update(existingGoal);
                    }
                });
            } else {
                // Создание новой цели
                goal = new Goal(name, targetAmount, priority, createdAt);
                goalViewModel.insert(goal);
            }

            finish();

        } catch (NumberFormatException e) {
            binding.etTargetAmount.setError("Неверный формат числа");
        }
    }

    private Priority getSelectedPriority() {
        int selectedId = binding.rgPriority.getCheckedRadioButtonId();

        if (selectedId == R.id.rb_high) {
            return Priority.HIGH;
        } else if (selectedId == R.id.rb_low) {
            return Priority.LOW;
        } else {
            return Priority.MEDIUM;
        }
    }
}
