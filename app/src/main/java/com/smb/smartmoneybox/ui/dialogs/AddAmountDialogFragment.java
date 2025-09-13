package com.smb.smartmoneybox.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Transaction;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.TransactionViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddAmountDialogFragment extends DialogFragment {

    private TextInputEditText etAmount;
    private GoalViewModel goalViewModel;
    private TransactionViewModel transactionViewModel;
    private String goalId;
    private String goalName;

    public static AddAmountDialogFragment newInstance(String goalId, String goalName) {
        AddAmountDialogFragment fragment = new AddAmountDialogFragment();
        Bundle args = new Bundle();
        args.putString("goal_id", goalId);
        args.putString("goal_name", goalName);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_amount, null);

        // Получаем аргументы
        Bundle args = getArguments();
        if (args != null) {
            goalId = args.getString("goal_id");
            goalName = args.getString("goal_name");
        }

        setupViewModels();
        initViews(view);
        setupListeners(view);

        builder.setView(view);
        if (goalName != null) {
            builder.setTitle("Добавить сумму для: " + goalName);
        } else {
            builder.setTitle("Добавить сумму");
        }

        return builder.create();
    }

    private void setupViewModels() {
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        goalViewModel = new ViewModelProvider(requireActivity(), factory).get(GoalViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionViewModel.class);
    }

    private void initViews(View view) {
        etAmount = view.findViewById(R.id.et_amount);
    }

    private void setupListeners(View view) {
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnAdd = view.findViewById(R.id.btn_add);

        btnCancel.setOnClickListener(v -> dismiss());

        btnAdd.setOnClickListener(v -> addAmountToGoal());
    }

    private void addAmountToGoal() {
        String amountText = etAmount.getText().toString().trim();

        if (amountText.isEmpty()) {
            etAmount.setError("Введите сумму");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                etAmount.setError("Сумма должна быть положительной");
                return;
            }

            if (goalId == null) {
                Toast.makeText(requireContext(), "Ошибка: цель не определена", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }

            // Обновляем цель
            goalViewModel.getGoalById(goalId).observe(this, goal -> {
                if (goal != null) {
                    double newAmount = goal.getCurrentAmount() + amount;
                    if (newAmount > goal.getTargetAmount()) {
                        newAmount = goal.getTargetAmount(); // Не превышаем целевую сумму
                    }
                    goal.setCurrentAmount(newAmount);
                    goalViewModel.update(goal);
                }
            });

            // Создаем транзакцию
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Transaction transaction = new Transaction(goalId, amount, currentDate);
            transactionViewModel.insert(transaction);

            Toast.makeText(requireContext(), "Сумма добавлена", Toast.LENGTH_SHORT).show();
            dismiss();

        } catch (NumberFormatException e) {
            etAmount.setError("Неверный формат числа");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        etAmount = null;
    }
}
