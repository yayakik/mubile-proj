package com.example.expensetrackingapp.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, ExpenseEntity::class], version = 2)
abstract class ExpenseUserDataBase : RoomDatabase() {

    abstract fun getUserDao(): UserDAO
    abstract fun getExpenseDao(): ExpenseDAO

    companion object {
        @Volatile
        private var INSTANCE: ExpenseUserDataBase? = null

        fun getDatabase(context: Context): ExpenseUserDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseUserDataBase::class.java,
                    "expense_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
