package com.smb.smartmoneybox.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Expense;
import com.smb.smartmoneybox.ui.adapters.ExpenseAdapter;
import com.smb.smartmoneybox.ui.dialogs.IncomeInputDialogFragment;
import com.smb.smartmoneybox.utils.SavingsCalculator;
import com.smb.smartmoneybox.viewmodel.ExpenseViewModel;
import com.smb.smartmoneybox.viewmodel.MonthlyIncomeViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseManagementActivity extends AppCompatActivity implements ExpenseAdapter.OnItemClickListener {

    private RecyclerView rvExpenses;
    private TextView tvMonthlyIncome, tvTotalExpenses, tvSavingsCapacity;
    private TextView tvRecommendedSavings, tvSafeSavings;
    private Button btnSetIncome, btnCalculateSavings;
    private FloatingActionButton fabAddExpense;

    private ExpenseViewModel expenseViewModel;
    private MonthlyIncomeViewModel monthlyIncomeViewModel;
    private ExpenseAdapter expenseAdapter;
    private SavingsCalculator savingsCalculator = new SavingsCalculator();

    private double currentIncomeValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_management);

        initViews();
        setupViewModels();
        setupRecyclerView();
        setupSwipeToDelete();
        setupListeners();
        observeData();
    }

    private void setupViewModels() {
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        expenseViewModel = new ViewModelProvider(this, factory).get(ExpenseViewModel.class);
        monthlyIncomeViewModel = new ViewModelProvider(this, factory).get(MonthlyIncomeViewModel.class);
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expense = expenseAdapter.getExpenseAt(position);
                expenseViewModel.delete(expense);
                Toast.makeText(ExpenseManagementActivity.this, "Трата удалена", Toast.LENGTH_SHORT).show();

                // Пересчитываем после удаления
                calculateSavings();
            }
        }).attachToRecyclerView(rvExpenses);
    }

    private void observeData() {
        expenseViewModel.getAllExpenses().observe(this, expenses -> {
            expenseAdapter.setExpenses(expenses);
            calculateSavings();
        });

        monthlyIncomeViewModel.getCurrentIncome().observe(this, monthlyIncome -> {
            if (monthlyIncome != null) {
                currentIncomeValue = monthlyIncome.getAmount();
                monthlyIncomeViewModel.updateCurrentIncomeValue(currentIncomeValue);
                updateIncomeDisplay(currentIncomeValue);
                calculateSavings();
            }
        });

        monthlyIncomeViewModel.getCurrentIncomeValue().observe(this, income -> {
            if (income != null) {
                currentIncomeValue = income;
            }
        });

        expenseViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIncomeDialog() {
        IncomeInputDialogFragment dialog = new IncomeInputDialogFragment();

        // Используем текущее значение из переменной
        dialog.setInitialIncome(currentIncomeValue);

        dialog.setIncomeInputListener(income -> {
            monthlyIncomeViewModel.setIncome(income); // Сохраняем в БД
            monthlyIncomeViewModel.updateCurrentIncomeValue(income); // Обновляем LiveData
            calculateSavings();
        });

        dialog.show(getSupportFragmentManager(), "IncomeInputDialog");
    }

    private void updateIncomeDisplay(double income) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        tvMonthlyIncome.setText(format.format(income));
    }

    private void calculateSavings() {
        List<Expense> expenses = expenseAdapter.getExpenses();

        if (expenses == null || currentIncomeValue <= 0) return;

        double totalMonthlyExpenses = savingsCalculator.calculateTotalMonthlyExpenses(expenses);
        double savingsCapacity = savingsCalculator.calculateMonthlySavingsCapacity(currentIncomeValue, expenses);
        double recommended = savingsCalculator.calculateRecommendedSavings(currentIncomeValue, totalMonthlyExpenses);
        double safe = savingsCalculator.calculateSafeSavings(currentIncomeValue, totalMonthlyExpenses);

        // Сохраняем для использования в главном активити
        SharedPreferences prefs = getSharedPreferences("SmartMoneyBoxPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("savings_capacity", (float) savingsCapacity);
        editor.putFloat("recommended", (float) recommended);
        editor.putFloat("safe", (float) safe);
        editor.apply();

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

        tvTotalExpenses.setText(format.format(totalMonthlyExpenses));
        tvSavingsCapacity.setText(format.format(savingsCapacity));
        tvRecommendedSavings.setText(format.format(recommended));
        tvSafeSavings.setText(format.format(safe));
    }

    private void initViews() {
        rvExpenses = findViewById(R.id.rv_expenses);
        tvMonthlyIncome = findViewById(R.id.tv_monthly_income);
        tvTotalExpenses = findViewById(R.id.tv_total_expenses);
        tvSavingsCapacity = findViewById(R.id.tv_savings_capacity);
        tvRecommendedSavings = findViewById(R.id.tv_recommended_savings);
        tvSafeSavings = findViewById(R.id.tv_safe_savings);
        btnSetIncome = findViewById(R.id.btn_set_income);
        btnCalculateSavings = findViewById(R.id.btn_calculate_savings);
        fabAddExpense = findViewById(R.id.fab_add_expense);
    }

    private void setupRecyclerView() {
        expenseAdapter = new ExpenseAdapter(this);
        expenseAdapter.setOnItemClickListener(this);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(expenseAdapter);
    }

    private void setupListeners() {
        btnSetIncome.setOnClickListener(v -> showIncomeDialog());
        btnCalculateSavings.setOnClickListener(v -> calculateSavings());
        fabAddExpense.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void showAddExpenseDialog() {
        Intent intent = new Intent(this, ExpenseEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Expense expense) {
        Intent intent = new Intent(this, ExpenseEditActivity.class);
        intent.putExtra("expense_id", expense.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Expense expense) {
        expenseViewModel.delete(expense);
        Toast.makeText(this, "Трата удалена", Toast.LENGTH_SHORT).show();
    }
}
