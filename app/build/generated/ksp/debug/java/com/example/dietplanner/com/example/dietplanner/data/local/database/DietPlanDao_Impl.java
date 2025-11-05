package com.example.dietplanner.com.example.dietplanner.data.local.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DietPlanDao_Impl implements DietPlanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DietPlanEntity> __insertionAdapterOfDietPlanEntity;

  private final EntityInsertionAdapter<DayPlanEntity> __insertionAdapterOfDayPlanEntity;

  private final EntityDeletionOrUpdateAdapter<DietPlanEntity> __deletionAdapterOfDietPlanEntity;

  private final EntityDeletionOrUpdateAdapter<DietPlanEntity> __updateAdapterOfDietPlanEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDayPlansForDiet;

  public DietPlanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDietPlanEntity = new EntityInsertionAdapter<DietPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `diet_plans` (`id`,`name`,`content`,`cleanedContent`,`createdAt`,`planType`,`userHeight`,`userWeight`,`userAge`,`userGender`,`isFavorite`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DietPlanEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getContent());
        statement.bindString(4, entity.getCleanedContent());
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindString(6, entity.getPlanType());
        statement.bindDouble(7, entity.getUserHeight());
        statement.bindDouble(8, entity.getUserWeight());
        statement.bindLong(9, entity.getUserAge());
        statement.bindString(10, entity.getUserGender());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(11, _tmp);
      }
    };
    this.__insertionAdapterOfDayPlanEntity = new EntityInsertionAdapter<DayPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `day_plans` (`id`,`dietPlanId`,`dayName`,`dayNumber`,`breakfast`,`lunch`,`dinner`,`snacks`,`exercise`,`hydration`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayPlanEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDietPlanId());
        statement.bindString(3, entity.getDayName());
        statement.bindLong(4, entity.getDayNumber());
        statement.bindString(5, entity.getBreakfast());
        statement.bindString(6, entity.getLunch());
        statement.bindString(7, entity.getDinner());
        statement.bindString(8, entity.getSnacks());
        statement.bindString(9, entity.getExercise());
        statement.bindString(10, entity.getHydration());
        statement.bindString(11, entity.getNotes());
      }
    };
    this.__deletionAdapterOfDietPlanEntity = new EntityDeletionOrUpdateAdapter<DietPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `diet_plans` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DietPlanEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDietPlanEntity = new EntityDeletionOrUpdateAdapter<DietPlanEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `diet_plans` SET `id` = ?,`name` = ?,`content` = ?,`cleanedContent` = ?,`createdAt` = ?,`planType` = ?,`userHeight` = ?,`userWeight` = ?,`userAge` = ?,`userGender` = ?,`isFavorite` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DietPlanEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getContent());
        statement.bindString(4, entity.getCleanedContent());
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindString(6, entity.getPlanType());
        statement.bindDouble(7, entity.getUserHeight());
        statement.bindDouble(8, entity.getUserWeight());
        statement.bindLong(9, entity.getUserAge());
        statement.bindString(10, entity.getUserGender());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteDayPlansForDiet = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM day_plans WHERE dietPlanId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDietPlan(final DietPlanEntity plan,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDietPlanEntity.insertAndReturnId(plan);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDayPlans(final List<DayPlanEntity> dayPlans,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDayPlanEntity.insert(dayPlans);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDietPlan(final DietPlanEntity plan,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDietPlanEntity.handle(plan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDietPlan(final DietPlanEntity plan,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDietPlanEntity.handle(plan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDayPlansForDiet(final long planId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDayPlansForDiet.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, planId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteDayPlansForDiet.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DietPlanEntity>> getAllDietPlans() {
    final String _sql = "SELECT * FROM diet_plans ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diet_plans"}, new Callable<List<DietPlanEntity>>() {
      @Override
      @NonNull
      public List<DietPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCleanedContent = CursorUtil.getColumnIndexOrThrow(_cursor, "cleanedContent");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfPlanType = CursorUtil.getColumnIndexOrThrow(_cursor, "planType");
          final int _cursorIndexOfUserHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userHeight");
          final int _cursorIndexOfUserWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeight");
          final int _cursorIndexOfUserAge = CursorUtil.getColumnIndexOrThrow(_cursor, "userAge");
          final int _cursorIndexOfUserGender = CursorUtil.getColumnIndexOrThrow(_cursor, "userGender");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<DietPlanEntity> _result = new ArrayList<DietPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DietPlanEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpCleanedContent;
            _tmpCleanedContent = _cursor.getString(_cursorIndexOfCleanedContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpPlanType;
            _tmpPlanType = _cursor.getString(_cursorIndexOfPlanType);
            final float _tmpUserHeight;
            _tmpUserHeight = _cursor.getFloat(_cursorIndexOfUserHeight);
            final float _tmpUserWeight;
            _tmpUserWeight = _cursor.getFloat(_cursorIndexOfUserWeight);
            final int _tmpUserAge;
            _tmpUserAge = _cursor.getInt(_cursorIndexOfUserAge);
            final String _tmpUserGender;
            _tmpUserGender = _cursor.getString(_cursorIndexOfUserGender);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _item = new DietPlanEntity(_tmpId,_tmpName,_tmpContent,_tmpCleanedContent,_tmpCreatedAt,_tmpPlanType,_tmpUserHeight,_tmpUserWeight,_tmpUserAge,_tmpUserGender,_tmpIsFavorite);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDietPlanById(final long planId,
      final Continuation<? super DietPlanEntity> $completion) {
    final String _sql = "SELECT * FROM diet_plans WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DietPlanEntity>() {
      @Override
      @Nullable
      public DietPlanEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCleanedContent = CursorUtil.getColumnIndexOrThrow(_cursor, "cleanedContent");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfPlanType = CursorUtil.getColumnIndexOrThrow(_cursor, "planType");
          final int _cursorIndexOfUserHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userHeight");
          final int _cursorIndexOfUserWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeight");
          final int _cursorIndexOfUserAge = CursorUtil.getColumnIndexOrThrow(_cursor, "userAge");
          final int _cursorIndexOfUserGender = CursorUtil.getColumnIndexOrThrow(_cursor, "userGender");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final DietPlanEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpCleanedContent;
            _tmpCleanedContent = _cursor.getString(_cursorIndexOfCleanedContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpPlanType;
            _tmpPlanType = _cursor.getString(_cursorIndexOfPlanType);
            final float _tmpUserHeight;
            _tmpUserHeight = _cursor.getFloat(_cursorIndexOfUserHeight);
            final float _tmpUserWeight;
            _tmpUserWeight = _cursor.getFloat(_cursorIndexOfUserWeight);
            final int _tmpUserAge;
            _tmpUserAge = _cursor.getInt(_cursorIndexOfUserAge);
            final String _tmpUserGender;
            _tmpUserGender = _cursor.getString(_cursorIndexOfUserGender);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _result = new DietPlanEntity(_tmpId,_tmpName,_tmpContent,_tmpCleanedContent,_tmpCreatedAt,_tmpPlanType,_tmpUserHeight,_tmpUserWeight,_tmpUserAge,_tmpUserGender,_tmpIsFavorite);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DietPlanEntity> getDietPlanByIdFlow(final long planId) {
    final String _sql = "SELECT * FROM diet_plans WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diet_plans"}, new Callable<DietPlanEntity>() {
      @Override
      @Nullable
      public DietPlanEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCleanedContent = CursorUtil.getColumnIndexOrThrow(_cursor, "cleanedContent");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfPlanType = CursorUtil.getColumnIndexOrThrow(_cursor, "planType");
          final int _cursorIndexOfUserHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userHeight");
          final int _cursorIndexOfUserWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeight");
          final int _cursorIndexOfUserAge = CursorUtil.getColumnIndexOrThrow(_cursor, "userAge");
          final int _cursorIndexOfUserGender = CursorUtil.getColumnIndexOrThrow(_cursor, "userGender");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final DietPlanEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpCleanedContent;
            _tmpCleanedContent = _cursor.getString(_cursorIndexOfCleanedContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpPlanType;
            _tmpPlanType = _cursor.getString(_cursorIndexOfPlanType);
            final float _tmpUserHeight;
            _tmpUserHeight = _cursor.getFloat(_cursorIndexOfUserHeight);
            final float _tmpUserWeight;
            _tmpUserWeight = _cursor.getFloat(_cursorIndexOfUserWeight);
            final int _tmpUserAge;
            _tmpUserAge = _cursor.getInt(_cursorIndexOfUserAge);
            final String _tmpUserGender;
            _tmpUserGender = _cursor.getString(_cursorIndexOfUserGender);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _result = new DietPlanEntity(_tmpId,_tmpName,_tmpContent,_tmpCleanedContent,_tmpCreatedAt,_tmpPlanType,_tmpUserHeight,_tmpUserWeight,_tmpUserAge,_tmpUserGender,_tmpIsFavorite);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DayPlanEntity>> getDayPlansForDiet(final long planId) {
    final String _sql = "SELECT * FROM day_plans WHERE dietPlanId = ? ORDER BY dayNumber";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, planId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_plans"}, new Callable<List<DayPlanEntity>>() {
      @Override
      @NonNull
      public List<DayPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDietPlanId = CursorUtil.getColumnIndexOrThrow(_cursor, "dietPlanId");
          final int _cursorIndexOfDayName = CursorUtil.getColumnIndexOrThrow(_cursor, "dayName");
          final int _cursorIndexOfDayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "dayNumber");
          final int _cursorIndexOfBreakfast = CursorUtil.getColumnIndexOrThrow(_cursor, "breakfast");
          final int _cursorIndexOfLunch = CursorUtil.getColumnIndexOrThrow(_cursor, "lunch");
          final int _cursorIndexOfDinner = CursorUtil.getColumnIndexOrThrow(_cursor, "dinner");
          final int _cursorIndexOfSnacks = CursorUtil.getColumnIndexOrThrow(_cursor, "snacks");
          final int _cursorIndexOfExercise = CursorUtil.getColumnIndexOrThrow(_cursor, "exercise");
          final int _cursorIndexOfHydration = CursorUtil.getColumnIndexOrThrow(_cursor, "hydration");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DayPlanEntity> _result = new ArrayList<DayPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayPlanEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDietPlanId;
            _tmpDietPlanId = _cursor.getLong(_cursorIndexOfDietPlanId);
            final String _tmpDayName;
            _tmpDayName = _cursor.getString(_cursorIndexOfDayName);
            final int _tmpDayNumber;
            _tmpDayNumber = _cursor.getInt(_cursorIndexOfDayNumber);
            final String _tmpBreakfast;
            _tmpBreakfast = _cursor.getString(_cursorIndexOfBreakfast);
            final String _tmpLunch;
            _tmpLunch = _cursor.getString(_cursorIndexOfLunch);
            final String _tmpDinner;
            _tmpDinner = _cursor.getString(_cursorIndexOfDinner);
            final String _tmpSnacks;
            _tmpSnacks = _cursor.getString(_cursorIndexOfSnacks);
            final String _tmpExercise;
            _tmpExercise = _cursor.getString(_cursorIndexOfExercise);
            final String _tmpHydration;
            _tmpHydration = _cursor.getString(_cursorIndexOfHydration);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new DayPlanEntity(_tmpId,_tmpDietPlanId,_tmpDayName,_tmpDayNumber,_tmpBreakfast,_tmpLunch,_tmpDinner,_tmpSnacks,_tmpExercise,_tmpHydration,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DietPlanEntity>> getFavoritePlans() {
    final String _sql = "SELECT * FROM diet_plans WHERE isFavorite = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diet_plans"}, new Callable<List<DietPlanEntity>>() {
      @Override
      @NonNull
      public List<DietPlanEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfCleanedContent = CursorUtil.getColumnIndexOrThrow(_cursor, "cleanedContent");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfPlanType = CursorUtil.getColumnIndexOrThrow(_cursor, "planType");
          final int _cursorIndexOfUserHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userHeight");
          final int _cursorIndexOfUserWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeight");
          final int _cursorIndexOfUserAge = CursorUtil.getColumnIndexOrThrow(_cursor, "userAge");
          final int _cursorIndexOfUserGender = CursorUtil.getColumnIndexOrThrow(_cursor, "userGender");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<DietPlanEntity> _result = new ArrayList<DietPlanEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DietPlanEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpCleanedContent;
            _tmpCleanedContent = _cursor.getString(_cursorIndexOfCleanedContent);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpPlanType;
            _tmpPlanType = _cursor.getString(_cursorIndexOfPlanType);
            final float _tmpUserHeight;
            _tmpUserHeight = _cursor.getFloat(_cursorIndexOfUserHeight);
            final float _tmpUserWeight;
            _tmpUserWeight = _cursor.getFloat(_cursorIndexOfUserWeight);
            final int _tmpUserAge;
            _tmpUserAge = _cursor.getInt(_cursorIndexOfUserAge);
            final String _tmpUserGender;
            _tmpUserGender = _cursor.getString(_cursorIndexOfUserGender);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            _item = new DietPlanEntity(_tmpId,_tmpName,_tmpContent,_tmpCleanedContent,_tmpCreatedAt,_tmpPlanType,_tmpUserHeight,_tmpUserWeight,_tmpUserAge,_tmpUserGender,_tmpIsFavorite);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDietPlansCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM diet_plans";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
