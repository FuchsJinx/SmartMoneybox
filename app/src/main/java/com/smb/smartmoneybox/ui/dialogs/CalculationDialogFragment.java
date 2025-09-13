package com.smb.smartmoneybox.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.utils.SavingsCalculator;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CalculationDialogFragment extends DialogFragment {

    private List<Goal> goals;
    private double monthlySavingsCapacity;
    double monthlyRecommended;
    double monthlySafe;

    public static CalculationDialogFragment newInstance(List<Goal> goals, double monthlySavingsCapacity, double monthlyRecommended, double monthlySafe) {
        CalculationDialogFragment fragment = new CalculationDialogFragment();
        fragment.setGoals(goals);
        fragment.setMonthlySavingsCapacity(monthlySavingsCapacity, monthlyRecommended, monthlySafe);
        return fragment;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public void setMonthlySavingsCapacity(double monthlySavingsCapacity, double monthlyRecommended, double monthlySafe) {
        this.monthlySavingsCapacity = monthlySavingsCapacity;
        this.monthlyRecommended = monthlyRecommended;
        this.monthlySafe = monthlySafe;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_calculation, null);

        TextView tvResults = view.findViewById(R.id.tv_calculation_results);
        Button btnOk = view.findViewById(R.id.btn_ok);

        String resultsText = calculateResults();
        tvResults.setText(resultsText);

        btnOk.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }

    private String calculateResults() {
        if (goals == null || goals.isEmpty() || monthlySavingsCapacity <= 0) {
            return "Недостаточно данных для расчета.\n\nУбедитесь, что:\n1. Добавлены цели\n2. Указан доход в калькуляторе трат\n3. Рассчитана возможность накоплений";
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        StringBuilder sb = new StringBuilder();

        sb.append("Ежемесячно можете откладывать: ").append(format.format(monthlySavingsCapacity)).append("\n");
        sb.append("Ежемесячно рекомендуемо откладывать: ").append(format.format(monthlyRecommended)).append("\n");
        sb.append("Ежемесячно безопасно откладывать: ").append(format.format(monthlySafe)).append("\n\n");

        for (Goal goal : goals) {
            if (goal.getCurrentAmount() < goal.getTargetAmount()) {
                double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
                int months = (int) Math.ceil(remaining / monthlySavingsCapacity);
                int months2 = (int) Math.ceil(remaining / monthlyRecommended);
                int months3 = (int) Math.ceil(remaining / monthlySafe);

                sb.append("Цель: ").append(goal.getName()).append("\n");
                sb.append("Осталось собрать: ").append(format.format(remaining)).append("\n");
                sb.append("Примерный срок: ").append(months).append(" ").append(getMonthWord(months)).append("\n");
                sb.append("Примерный рекомендуемый срок: ").append(months2).append(" ").append(getMonthWord(months2)).append("\n");
                sb.append("Примерный безопасный срок: ").append(months3).append(" ").append(getMonthWord(months3)).append("\n\n");
            }
        }

        return sb.toString();
    }

    private String getMonthWord(int months) {
        if (months % 10 == 1 && months % 100 != 11) {
            return "месяц";
        } else if (months % 10 >= 2 && months % 10 <= 4 && (months % 100 < 10 || months % 100 >= 20)) {
            return "месяца";
        } else {
            return "месяцев";
        }
    }
}
