package com.smb.smartmoneybox.ui.dialogs;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Transaction;
import com.smb.smartmoneybox.ui.adapters.DistributionAdapter;
import com.smb.smartmoneybox.viewmodel.DistributionViewModel;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.TransactionViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.util.Locale;
import java.util.Map;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DistributionDialogFragment extends androidx.fragment.app.DialogFragment {

    private RecyclerView rvDistribution;
    private TextView tvRemaining;
    private Button btnConfirm;
    private DistributionAdapter adapter;

    private GoalViewModel goalViewModel;
    private TransactionViewModel transactionViewModel;
    private DistributionViewModel distributionViewModel;

    private double initialIncome;
    private Map<String, Double> originalDistribution;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_distribution, container, false);

        rvDistribution = view.findViewById(R.id.rv_distribution);
        tvRemaining = view.findViewById(R.id.tv_remaining);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        goalViewModel = new ViewModelProvider(requireActivity(), factory).get(GoalViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);
        distributionViewModel = new ViewModelProvider(requireActivity(), factory).get(DistributionViewModel.class);

        // Получаем initialIncome из аргументов
        Bundle args = getArguments();
        if (args != null) {
            initialIncome = args.getDouble("income", 0);
        }

        setupRecyclerView();
        setupListeners();
        observeData();
    }

    private void setupRecyclerView() {
        adapter = new DistributionAdapter(requireContext()); // Передаем контекст
        rvDistribution.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDistribution.setAdapter(adapter);
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(v -> applyDistribution());
        requireView().findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
    }

    private void observeData() {
        distributionViewModel.getDistributionResult().observe(getViewLifecycleOwner(), distribution -> {
            if (distribution != null) {
                originalDistribution = distribution;
                goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
                    adapter.setDistributionData(goals, distribution);
                    updateRemainingAmount(distribution);
                });
            }
        });

        distributionViewModel.getDistributionError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRemainingAmount(Map<String, Double> distribution) {
        double totalDistributed = distribution.values().stream().mapToDouble(Double::doubleValue).sum();
        double remaining = initialIncome - totalDistributed;

        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        tvRemaining.setText(String.format("Остаток: %s", format.format(remaining)));
    }

    private void applyDistribution() {
        Map<String, Double> currentDistribution = adapter.getCurrentDistribution();

        // Проверяем, что сумма распределения не превышает доход
        double totalDistributed = currentDistribution.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalDistributed > initialIncome) {
            Toast.makeText(requireContext(), "Сумма распределения превышает доход", Toast.LENGTH_SHORT).show();
            return;
        }

        // Применяем распределение
        applyDistributionToGoals(currentDistribution);
        createTransactions(currentDistribution);

        Toast.makeText(requireContext(), "Средства распределены", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void applyDistributionToGoals(Map<String, Double> distribution) {
        goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
            for (Goal goal : goals) {
                Double amount = distribution.get(goal.getId());
                if (amount != null && amount > 0) {
                    double newAmount = goal.getCurrentAmount() + amount;
                    if (newAmount > goal.getTargetAmount()) {
                        newAmount = goal.getTargetAmount(); // Не превышаем целевую сумму
                    }
                    goal.setCurrentAmount(newAmount);
                    goalViewModel.update(goal);
                }
            }
        });
    }

    private void createTransactions(Map<String, Double> distribution) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (Map.Entry<String, Double> entry : distribution.entrySet()) {
            if (entry.getValue() > 0) {
                Transaction transaction = new Transaction(
                        entry.getKey(),
                        entry.getValue(),
                        currentDate
                );
                transactionViewModel.insert(transaction);
            }
        }
    }

    public static DistributionDialogFragment newInstance(double income) {
        DistributionDialogFragment fragment = new DistributionDialogFragment();
        Bundle args = new Bundle();
        args.putDouble("income", income);
        fragment.setArguments(args);
        return fragment;
    }
}