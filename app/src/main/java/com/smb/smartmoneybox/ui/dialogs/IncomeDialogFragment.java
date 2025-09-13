package com.smb.smartmoneybox.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.viewmodel.DistributionViewModel;
import com.smb.smartmoneybox.viewmodel.GoalViewModel;
import com.smb.smartmoneybox.viewmodel.ViewModelFactory;

public class IncomeDialogFragment extends DialogFragment {

    private GoalViewModel goalViewModel;
    private DistributionViewModel distributionViewModel;
    private EditText etIncome;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_income, null);

        setupViewModels();
        initViews(view);

        builder.setView(view)
                .setTitle("Ввод дохода")
                .setPositiveButton("Распределить", (dialog, which) -> {
                    // Обработка нажатия кнопки
                    processIncomeInput();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void setupViewModels() {
        ViewModelFactory factory = new ViewModelFactory(requireActivity().getApplication());
        goalViewModel = new ViewModelProvider(requireActivity(), factory).get(GoalViewModel.class);
        distributionViewModel = new ViewModelProvider(requireActivity(), factory).get(DistributionViewModel.class);
    }

    private void initViews(View view) {
        etIncome = view.findViewById(R.id.et_income);
    }

    private void processIncomeInput() {
        String incomeText = etIncome.getText().toString().trim();

        if (incomeText.isEmpty()) {
            Toast.makeText(requireContext(), "Введите сумму дохода", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double income = Double.parseDouble(incomeText);
            if (income <= 0) {
                Toast.makeText(requireContext(), "Сумма должна быть положительной", Toast.LENGTH_SHORT).show();
                return;
            }

            // Используем глобальный lifecycle вместо view lifecycle
            goalViewModel.getAllGoals().observe(this, goals -> {
                distributionViewModel.calculateDistribution(income, goals);

                // Создаем диалог распределения
                DistributionDialogFragment distributionDialog = DistributionDialogFragment.newInstance(income);
                distributionDialog.show(getParentFragmentManager(), "DistributionDialog");
            });

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Неверный формат числа", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        etIncome = null;
    }
}