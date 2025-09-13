package com.smb.smartmoneybox.ui.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Goal;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistributionAdapter extends RecyclerView.Adapter<DistributionAdapter.DistributionViewHolder> {

    private List<Goal> goals;
    private Map<String, Double> distribution;
    private Map<String, Double> currentDistribution = new HashMap<>();
    private final Context context;

    public DistributionAdapter(Context context) {
        this.context = context;
    }

    public void setDistributionData(List<Goal> goals, Map<String, Double> distribution) {
        this.goals = goals;
        this.distribution = distribution;
        this.currentDistribution = new HashMap<>(distribution);
        notifyDataSetChanged();
    }

    public Map<String, Double> getCurrentDistribution() {
        return currentDistribution;
    }

    @NonNull
    @Override
    public DistributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_distribution, parent, false);
        return new DistributionViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributionViewHolder holder, int position) {
        Goal goal = goals.get(position);
        Double amount = distribution.get(goal.getId());

        if (amount != null) {
            holder.bind(goal, amount);
        }
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    class DistributionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGoalName;
        private final TextView tvSuggestedAmount;
        private final EditText etActualAmount;
        private final TextView tvRemaining;
        private final Context context;

        DistributionViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            tvGoalName = itemView.findViewById(R.id.tv_goal_name);
            tvSuggestedAmount = itemView.findViewById(R.id.tv_suggested_amount);
            etActualAmount = itemView.findViewById(R.id.et_actual_amount);
            tvRemaining = itemView.findViewById(R.id.tv_remaining);
        }

        void bind(Goal goal, Double suggestedAmount) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

            tvGoalName.setText(goal.getName());
            tvSuggestedAmount.setText(context.getString(R.string.suggested_amount, format.format(suggestedAmount)));

            double remaining = goal.getTargetAmount() - goal.getCurrentAmount();
            tvRemaining.setText(context.getString(R.string.remaining_amount, format.format(remaining)));

            etActualAmount.setText(String.valueOf(suggestedAmount));
            currentDistribution.put(goal.getId(), suggestedAmount);

            etActualAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double amount = Double.parseDouble(s.toString());
                        currentDistribution.put(goal.getId(), amount);
                    } catch (NumberFormatException e) {
                        currentDistribution.put(goal.getId(), 0.0);
                    }
                }
            });
        }
    }
}
