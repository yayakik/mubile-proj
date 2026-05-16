package com.example.expensetrackingapp.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "expensetable",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val expenseid :Int=0,
    val userId: Int, // foreign key
    val category : String,
    val date : String,
    val amount : Double
)
