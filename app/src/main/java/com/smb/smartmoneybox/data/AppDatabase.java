package com.smb.smartmoneybox.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.smb.smartmoneybox.data.dao.ExpenseDao;
import com.smb.smartmoneybox.data.dao.GoalDao;
import com.smb.smartmoneybox.data.dao.MonthlyIncomeDao;
import com.smb.smartmoneybox.data.dao.TransactionDao;
import com.smb.smartmoneybox.data.entities.Expense;
import com.smb.smartmoneybox.data.entities.Goal;
import com.smb.smartmoneybox.data.entities.MonthlyIncome;
import com.smb.smartmoneybox.data.entities.Transaction;
import com.smb.smartmoneybox.utils.Converters;
import com.smb.smartmoneybox.utils.ExpenseTypeConverter;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Goal.class, Transaction.class, Expense.class, MonthlyIncome.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class, ExpenseTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    // Миграция с версии 1 на 2 (добавление таблицы expenses)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Создаем таблицу expenses
            database.execSQL("CREATE TABLE expenses (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "name TEXT, " +
                    "amount REAL NOT NULL, " +
                    "type TEXT, " +
                    "createdAt TEXT)");
        }
    };
    // Миграция с версии 2 на 3 (добавление таблицы monthly_income)
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE monthly_income (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "updatedAt TEXT)");
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_moneybox_db"
                    )
                    .addMigrations(MIGRATION_2_3) // Добавляем миграцию
                    .fallbackToDestructiveMigration() // На случай ошибок миграции
                    .build();
        }
        return instance;
    }

    public abstract GoalDao goalDao();
    public abstract TransactionDao transactionDao();
    public abstract ExpenseDao expenseDao();
    public abstract MonthlyIncomeDao monthlyIncomeDao();
}