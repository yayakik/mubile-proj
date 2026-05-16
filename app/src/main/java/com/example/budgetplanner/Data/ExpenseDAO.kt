package com.example.expensetrackingapp.Data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {
    @Query("Select * From expensetable Where userId =:uid")
    suspend fun get_all_Expense_aboutUser(uid:Int): List<ExpenseEntity>

    @Query("Select * From expensetable Where userId =:uid and date = :dt")
    suspend fun get_all_Expense_aboutUser_WithDate(uid:Int,dt:String): List<ExpenseEntity>

    @Insert
    suspend fun addExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expensetable WHERE date = :dt")
    suspend fun deleteExpensePerDate(dt: String)

    @Query("DELETE FROM expensetable WHERE expenseid = :id")
    suspend fun deleteExpensePerId(id: Int)
}