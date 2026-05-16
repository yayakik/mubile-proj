package com.example.expensetrackingapp.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="usertable")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int =0,
    val name : String,
    val email : String,
    val Password : String?,
    val budget : Double,
    val totExpense: Double,
    val isGoogleUser: Boolean = false, // NEW FLAG

)
