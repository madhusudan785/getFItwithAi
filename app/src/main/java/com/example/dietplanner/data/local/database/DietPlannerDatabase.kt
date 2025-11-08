package com.example.dietplanner.com.example.dietplanner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dietplanner.com.example.dietplanner.data.model.ReminderEntity

@Database(
    entities = [DietPlanEntity::class, DayPlanEntity::class, ReminderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DietPlannerDatabase : RoomDatabase() {

    abstract fun dietPlanDao(): DietPlanDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: DietPlannerDatabase? = null

        fun getDatabase(context: Context): DietPlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DietPlannerDatabase::class.java,
                    "diet_planner_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
