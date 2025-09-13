package com.smb.smartmoneybox.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.smb.smartmoneybox.R;

public class IncomeInputDialogFragment extends DialogFragment {

    public interface IncomeInputListener {
        void onIncomeInput(double income);
    }

    private IncomeInputListener listener;
    private double initialIncome = 0;

    public void setIncomeInputListener(IncomeInputListener listener) {
        this.listener = listener;
    }

    public void setInitialIncome(double income) {
        this.initialIncome = income;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_income_input, null);

        EditText etIncome = view.findViewById(R.id.et_income);

        // Устанавливаем начальное значение дохода
        if (initialIncome > 0) {
            etIncome.setText(String.valueOf(initialIncome));
        }

        builder.setView(view)
                .setTitle("Введите месячный доход")
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String incomeText = etIncome.getText().toString().trim();
                    processIncomeInput(incomeText);
                })
                .setNegativeButton("Отмена", null);

        return builder.create();
    }

    private void processIncomeInput(String incomeText) {
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

            if (listener != null) {
                listener.onIncomeInput(income);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Неверный формат числа", Toast.LENGTH_SHORT).show();
        }
    }
}