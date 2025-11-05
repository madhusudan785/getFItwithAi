package com.example.dietplanner.com.example.dietplanner.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DietPlannerDatabase_Impl extends DietPlannerDatabase {
  private volatile DietPlanDao _dietPlanDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `diet_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `content` TEXT NOT NULL, `cleanedContent` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `planType` TEXT NOT NULL, `userHeight` REAL NOT NULL, `userWeight` REAL NOT NULL, `userAge` INTEGER NOT NULL, `userGender` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `day_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dietPlanId` INTEGER NOT NULL, `dayName` TEXT NOT NULL, `dayNumber` INTEGER NOT NULL, `breakfast` TEXT NOT NULL, `lunch` TEXT NOT NULL, `dinner` TEXT NOT NULL, `snacks` TEXT NOT NULL, `exercise` TEXT NOT NULL, `hydration` TEXT NOT NULL, `notes` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '33309cce78f552afc7d246bb5c249cd0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `diet_plans`");
        db.execSQL("DROP TABLE IF EXISTS `day_plans`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDietPlans = new HashMap<String, TableInfo.Column>(11);
        _columnsDietPlans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("cleanedContent", new TableInfo.Column("cleanedContent", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("planType", new TableInfo.Column("planType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("userHeight", new TableInfo.Column("userHeight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("userWeight", new TableInfo.Column("userWeight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("userAge", new TableInfo.Column("userAge", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("userGender", new TableInfo.Column("userGender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDietPlans.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDietPlans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDietPlans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDietPlans = new TableInfo("diet_plans", _columnsDietPlans, _foreignKeysDietPlans, _indicesDietPlans);
        final TableInfo _existingDietPlans = TableInfo.read(db, "diet_plans");
        if (!_infoDietPlans.equals(_existingDietPlans)) {
          return new RoomOpenHelper.ValidationResult(false, "diet_plans(com.example.dietplanner.com.example.dietplanner.data.local.database.DietPlanEntity).\n"
                  + " Expected:\n" + _infoDietPlans + "\n"
                  + " Found:\n" + _existingDietPlans);
        }
        final HashMap<String, TableInfo.Column> _columnsDayPlans = new HashMap<String, TableInfo.Column>(11);
        _columnsDayPlans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("dietPlanId", new TableInfo.Column("dietPlanId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("dayName", new TableInfo.Column("dayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("dayNumber", new TableInfo.Column("dayNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("breakfast", new TableInfo.Column("breakfast", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("lunch", new TableInfo.Column("lunch", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("dinner", new TableInfo.Column("dinner", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("snacks", new TableInfo.Column("snacks", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("exercise", new TableInfo.Column("exercise", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("hydration", new TableInfo.Column("hydration", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDayPlans.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDayPlans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDayPlans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDayPlans = new TableInfo("day_plans", _columnsDayPlans, _foreignKeysDayPlans, _indicesDayPlans);
        final TableInfo _existingDayPlans = TableInfo.read(db, "day_plans");
        if (!_infoDayPlans.equals(_existingDayPlans)) {
          return new RoomOpenHelper.ValidationResult(false, "day_plans(com.example.dietplanner.com.example.dietplanner.data.local.database.DayPlanEntity).\n"
                  + " Expected:\n" + _infoDayPlans + "\n"
                  + " Found:\n" + _existingDayPlans);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "33309cce78f552afc7d246bb5c249cd0", "61d5a7e7f092910cef705c5aec2c2d1f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "diet_plans","day_plans");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `diet_plans`");
      _db.execSQL("DELETE FROM `day_plans`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DietPlanDao.class, DietPlanDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DietPlanDao dietPlanDao() {
    if (_dietPlanDao != null) {
      return _dietPlanDao;
    } else {
      synchronized(this) {
        if(_dietPlanDao == null) {
          _dietPlanDao = new DietPlanDao_Impl(this);
        }
        return _dietPlanDao;
      }
    }
  }
}
