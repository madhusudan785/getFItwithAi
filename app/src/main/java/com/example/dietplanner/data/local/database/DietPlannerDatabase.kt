package com.example.dietplanner.com.example.dietplanner.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DietPlanEntity::class, DayPlanEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DietPlannerDatabase : RoomDatabase() {

    abstract fun dietPlanDao(): DietPlanDao

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
