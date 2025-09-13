package com.smb.smartmoneybox.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.Priority;
import com.smb.smartmoneybox.databinding.ItemGoalBinding;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goals;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Goal goal);
        void onEditClick(Goal goal);
        void onDeleteClick(Goal goal);
    }

    public GoalAdapter(Context context) {
        this.context = context;
    }

    public void setGoals(List<Goal> goals) {
        if (goals != null) {
            // Сортируем цели по приоритету
            Collections.sort(goals, new GoalPriorityComparator());
        }
        this.goals = goals;
        notifyDataSetChanged();
    }

    public Goal getGoalAt(int position) {
        return goals.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Компаратор для сортировки по приоритету
    private static class GoalPriorityComparator implements Comparator<Goal> {
        @Override
        public int compare(Goal g1, Goal g2) {
            // Сначала сортируем по приоритету (HIGH -> MEDIUM -> LOW)
            int priorityComparison = Integer.compare(
                    g2.getPriority().getValue(),
                    g1.getPriority().getValue()
            );

            // Если приоритет одинаковый, сортируем по дате создания (новые сначала)
            if (priorityComparison == 0) {
                return g2.getCreatedAt().compareTo(g1.getCreatedAt());
            }

            return priorityComparison;
        }
    }

    // Дополнительные методы сортировки
    public void sortByPriority() {
        if (goals != null) {
            Collections.sort(goals, new GoalPriorityComparator());
            notifyDataSetChanged();
        }
    }

    public void sortByDate() {
        if (goals != null) {
            Collections.sort(goals, (g1, g2) ->
                    g2.getCreatedAt().compareTo(g1.getCreatedAt())
            );
            notifyDataSetChanged();
        }
    }

    public void sortByName() {
        if (goals != null) {
            Collections.sort(goals, (g1, g2) ->
                    g1.getName().compareToIgnoreCase(g2.getName())
            );
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new GoalViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private final ItemGoalBinding binding;

        GoalViewHolder(ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Обработка клика по всей карточке
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(goals.get(position));
                }
            });

            // Обработка долгого нажатия для редактирования
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(goals.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(Goal goal) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

            binding.tvGoalName.setText(goal.getName());
            binding.tvAmount.setText(String.format("%s / %s",
                    format.format(goal.getCurrentAmount()),
                    format.format(goal.getTargetAmount())));

            // Progress
            int progress = (int) ((goal.getCurrentAmount() / goal.getTargetAmount()) * 100);
            binding.progressCircular.setProgress(progress);
            binding.progressBar.setProgress(progress);

            // Priority
            binding.tvPriority.setText(getPriorityString(goal.getPriority()));
            int priorityColor = getPriorityColor(goal.getPriority());
            binding.tvPriority.setBackgroundColor(priorityColor);
        }

        private String getPriorityString(Priority priority) {
            switch (priority) {
                case HIGH: return "ВЫСОКИЙ";
                case MEDIUM: return "СРЕДНИЙ";
                case LOW: return "НИЗКИЙ";
                default: return "";
            }
        }

        private int getPriorityColor(Priority priority) {
            switch (priority) {
                case HIGH:
                    return context.getColor(R.color.priority_high);
                case MEDIUM:
                    return context.getColor(R.color.priority_medium);
                case LOW:
                    return context.getColor(R.color.priority_low);
                default:
                    return context.getColor(R.color.priority_medium);
            }
        }
    }
}
