package com.smb.smartmoneybox.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.databinding.ActivityMainBinding;
import com.smb.smartmoneybox.ui.adapters.GoalAdapter;
import com.smb.smartmoneybox.ui.dialogs.CalculationDialogFragment;
import com.smb.smartmoneybox.ui.dialogs.IncomeDialogFragment;
import com.smb.smartmoneybox.utils.NotificationScheduler;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.util.List;


public class MainActivity extends AppCompatActivity implements GoalAdapter.OnItemClickListener {

    private ActivityMainBinding binding;
    private GoalViewModel goalViewModel;
    private GoalAdapter goalAdapter;
    private FloatingActionButton fabCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Запускаем планировщик уведомлений
        NotificationScheduler.scheduleMonthlyNotification(this);

        setSupportActionBar(binding.toolbar);
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        observeData();
    }

    private void setupViewModel() {
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        goalViewModel = new ViewModelProvider(this, factory).get(GoalViewModel.class);
    }

    private void setupRecyclerView() {
        goalAdapter = new GoalAdapter(this);
        goalAdapter.setOnItemClickListener(this);
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(goalAdapter);
        goalAdapter.sortByPriority();

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Goal goal = goalAdapter.getGoalAt(position);
                goalViewModel.delete(goal);
                Snackbar.make(binding.getRoot(), "Цель удалена", Snackbar.LENGTH_LONG)
                        .setAction("Отмена", v -> goalViewModel.insert(goal))
                        .show();
            }
        }).attachToRecyclerView(binding.rvGoals);
    }

    private void setupListeners() {
        binding.fabAddGoal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoalEditActivity.class);
            startActivity(intent);
        });

        binding.btnIncome.setOnClickListener(v -> {
            IncomeDialogFragment dialog = new IncomeDialogFragment();
            dialog.show(getSupportFragmentManager(), "IncomeDialog");
        });

        // Новая кнопка управления тратами
        binding.btnExpenses.setOnClickListener(v -> {
            openExpenseManagement();
        });

        // Новая кнопка расчета
        binding.fabCalculate.setOnClickListener(v -> {
            showCalculationDialog();
        });

//        binding.btnTestNotification.setOnClickListener(v -> {
//            checkAndRequestNotificationPermission();
//        });
    }

    private void showCalculationDialog() {
        // Получаем сохраненные данные о возможности накоплений
        SharedPreferences prefs = getSharedPreferences("SmartMoneyBoxPrefs", MODE_PRIVATE);
        double savingsCapacity = prefs.getFloat("savings_capacity", 0f);
        double recommended = prefs.getFloat("recommended", 0f);
        double safe = prefs.getFloat("safe", 0f);

        if (savingsCapacity <= 0) {
            Toast.makeText(this, "Сначала рассчитайте возможность накоплений в разделе 'Траты'", Toast.LENGTH_LONG).show();
            return;
        }

        // Используем текущие цели из адаптера
        List<Goal> goals = goalAdapter.getGoals();

        CalculationDialogFragment dialog = CalculationDialogFragment.newInstance(goals, savingsCapacity, recommended, safe);
        dialog.show(getSupportFragmentManager(), "CalculationDialog");
    }

    private void openExpenseManagement() {
        Intent intent = new Intent(MainActivity.this, ExpenseManagementActivity.class);
        startActivity(intent);
    }

    private void observeData() {
        goalViewModel.getAllGoals().observe(this, goals -> {
            goalAdapter.setGoals(goals);
            binding.rvGoals.setVisibility(goals.isEmpty() ? View.GONE : View.VISIBLE);
        });

        goalViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(Goal goal) {
        Intent intent = new Intent(this, GoalDetailActivity.class);
        intent.putExtra("goal_id", goal.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Goal goal) {
        Intent intent = new Intent(this, GoalEditActivity.class);
        intent.putExtra("goal_id", goal.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Goal goal) {
        goalViewModel.delete(goal);
        Snackbar.make(binding.getRoot(), "Цель удалена", Snackbar.LENGTH_LONG).show();
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001);
            } else {
                // Разрешение уже есть
                NotificationScheduler.testNotification(this);
            }
        } else {
            // Для Android < 13 разрешение не нужно
            NotificationScheduler.testNotification(this);
        }
    }

    // Обработка результата запроса разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationScheduler.testNotification(this);
            } else {
                Toast.makeText(this, "Разрешение на уведомления не получено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}