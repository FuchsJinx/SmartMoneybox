package com.smb.smartmoneybox.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.ui.adapters.TransactionAdapter;
import com.smb.smartmoneybox.ui.dialogs.AddAmountDialogFragment;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.TransactionViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.text.NumberFormat;
import java.util.Locale;

public class GoalDetailActivity extends AppCompatActivity {

    private CircularProgressBar progressCircular;
    private TextView tvGoalName, tvPriority, tvAmount;
    private Button btnAddAmount, btnEditGoal;
    private RecyclerView rvTransactions;

    private GoalViewModel goalViewModel;
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter transactionAdapter;

    private String goalId;
    private String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);

        goalId = getIntent().getStringExtra("goal_id");
        if (goalId == null) {
            finish();
            return;
        }

        initViews();
        setupViewModels();
        setupRecyclerView();
        setupListeners();
        observeData();
    }

    private void initViews() {
        progressCircular = findViewById(R.id.progress_circular);
        tvGoalName = findViewById(R.id.tv_goal_name);
        tvPriority = findViewById(R.id.tv_priority);
        tvAmount = findViewById(R.id.tv_amount);
        btnAddAmount = findViewById(R.id.btn_add_amount);
        btnEditGoal = findViewById(R.id.btn_edit_goal);
        rvTransactions = findViewById(R.id.rv_transactions);
    }

    private void setupViewModels() {
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        goalViewModel = new ViewModelProvider(this, factory).get(GoalViewModel.class);
        transactionViewModel = new ViewModelProvider(this, factory).get(TransactionViewModel.class);
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(this); // Передаем контекст
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);
    }

    private void setupListeners() {
        btnAddAmount.setOnClickListener(v -> showAddAmountDialog());
        btnEditGoal.setOnClickListener(v -> {
            Intent intent = new Intent(this, GoalEditActivity.class);
            intent.putExtra("goal_id", goalId);
            startActivity(intent);
        });
    }

    private void observeData() {
        goalViewModel.getGoalById(goalId).observe(this, goal -> {
            if (goal != null) {
                goalName = goal.getName(); // Сохраняем название цели
                updateGoalUI(goal);
            }
        });

        transactionViewModel.getTransactionsByGoalId(goalId).observe(this, transactions -> {
            transactionAdapter.setTransactions(transactions);
        });
    }

    private void updateGoalUI(Goal goal) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

        tvGoalName.setText(goal.getName());
        tvPriority.setText(goal.getPriority().toString());
        tvAmount.setText(String.format("%s / %s",
                format.format(goal.getCurrentAmount()),
                format.format(goal.getTargetAmount())));

        int progress = (int) ((goal.getCurrentAmount() / goal.getTargetAmount()) * 100);
        progressCircular.setProgress(progress);
    }

    private void showAddAmountDialog() {
        if (goalId != null) {
            AddAmountDialogFragment dialog = AddAmountDialogFragment.newInstance(goalId, goalName);
            dialog.show(getSupportFragmentManager(), "AddAmountDialog");
        } else {
            Toast.makeText(this, "Ошибка: цель не определена", Toast.LENGTH_SHORT).show();
        }
    }

}