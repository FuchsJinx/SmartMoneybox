package com.smb.smartmoneybox.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.smb.smartmoneybox.data.entities.MonthlyIncome;

@Dao
public interface MonthlyIncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MonthlyIncome income);

    @Query("SELECT * FROM monthly_income WHERE id = 'current_income'")
    LiveData<MonthlyIncome> getCurrentIncome();
}