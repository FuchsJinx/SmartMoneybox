package com.smb.smartmoneybox.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.databinding.ActivityExpenseEditBinding;
import com.smb.smartmoneybox.data.entities.Expense;
import com.smb.smartmoneybox.viewmodel.ExpenseViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseEditActivity extends AppCompatActivity {

    private ActivityExpenseEditBinding binding;
    private ExpenseViewModel expenseViewModel;
    private String existingExpenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupListeners();
        loadExistingExpense();
    }

    private void setupViewModel() {
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        expenseViewModel = new ViewModelProvider(this, factory).get(ExpenseViewModel.class);
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> saveExpense());
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void loadExistingExpense() {
        existingExpenseId = getIntent().getStringExtra("expense_id");
        if (existingExpenseId != null) {
            expenseViewModel.getExpenseById(existingExpenseId).observe(this, expense -> {
                if (expense != null) {
                    binding.etExpenseName.setText(expense.getName());
                    binding.etAmount.setText(String.valueOf(expense.getAmount()));

                    switch (expense.getType()) {
                        case DAILY:
                            binding.rbDaily.setChecked(true);
                            break;
                        case WEEKLY:
                            binding.rbWeekly.setChecked(true);
                            break;
                        case MONTHLY:
                            binding.rbMonthly.setChecked(true);
                            break;
                    }
                }
            });
        }
    }

    private void saveExpense() {
        String name = binding.etExpenseName.getText().toString().trim();
        String amountText = binding.etAmount.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etExpenseName.setError("Введите название траты");
            return;
        }

        if (amountText.isEmpty()) {
            binding.etAmount.setError("Введите сумму");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                binding.etAmount.setError("Сумма должна быть положительной");
                return;
            }

            Expense.ExpenseType type = getSelectedExpenseType();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (existingExpenseId != null) {
                // Редактирование существующей траты
                expenseViewModel.getExpenseById(existingExpenseId).observe(this, existingExpense -> {
                    if (existingExpense != null) {
                        existingExpense.setName(name);
                        existingExpense.setAmount(amount);
                        existingExpense.setType(type);
                        expenseViewModel.update(existingExpense);
                    }
                });
            } else {
                // Создание новой траты
                Expense expense = new Expense(name, amount, type, createdAt);
                expenseViewModel.insert(expense);
            }

            finish();

        } catch (NumberFormatException e) {
            binding.etAmount.setError("Неверный формат числа");
        }
    }

    private Expense.ExpenseType getSelectedExpenseType() {
        int selectedId = binding.rgExpenseType.getCheckedRadioButtonId();

        if (selectedId == R.id.rb_daily) {
            return Expense.ExpenseType.DAILY;
        } else if (selectedId == R.id.rb_monthly) {
            return Expense.ExpenseType.MONTHLY;
        } else {
            return Expense.ExpenseType.WEEKLY;
        }
    }
}
