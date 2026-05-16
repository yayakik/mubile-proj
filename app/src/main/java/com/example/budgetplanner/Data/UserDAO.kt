package com.example.expensetrackingapp.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDAO {
    @Query("Select * From usertable")
    suspend fun getAllUsers():List<UserEntity> //For Admin

    @Query("SELECT * FROM usertable WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM usertable WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?
    // In your UserDao.kt file
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: UserEntity): Long // It must return Long

    @Delete
    suspend fun DeleteUser(user: UserEntity)

    @Query("SELECT * FROM usertable WHERE email = :email AND Password = :password ")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("UPDATE usertable SET budget = :newBudget WHERE userId = :id")
    suspend fun updateBudget(id: Int, newBudget: Double)

    @Query("UPDATE usertable SET password = :pass WHERE userId = :id")
    suspend fun updatePassword(id: Int, pass: String)
    @Query("UPDATE usertable SET totExpense = :newTot WHERE userId = :id")
    suspend fun updateTotExpense(id: Int, newTot: Double)

}