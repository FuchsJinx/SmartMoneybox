package com.smb.smartmoneybox.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smb.smartmoneybox.R;
import com.smb.smartmoneybox.data.entities.Expense;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
        void onDeleteClick(Expense expense);
    }

    public ExpenseAdapter(Context context) {
        this.context = context;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenses != null ? expenses.size() : 0;
    }

    public Expense getExpenseAt(int position) {
        if (expenses != null && position >= 0 && position < expenses.size()) {
            return expenses.get(position);
        }
        return null;
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvAmount;
        private final TextView tvType;
        private final ImageButton btnDelete;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvType = itemView.findViewById(R.id.tv_type);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(expenses.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(expenses.get(position));
                }
            });
        }

        void bind(Expense expense) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

            tvName.setText(expense.getName());
            tvAmount.setText(format.format(expense.getAmount()));
            tvType.setText(getTypeString(expense.getType()));
        }

        private String getTypeString(Expense.ExpenseType type) {
            switch (type) {
                case DAILY: return "Ежедневно";
                case WEEKLY: return "Еженедельно";
                case MONTHLY: return "Ежемесячно";
                default: return "";
            }
        }
    }
}