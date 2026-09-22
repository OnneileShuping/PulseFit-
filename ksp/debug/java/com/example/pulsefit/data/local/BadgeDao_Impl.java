package com.example.pulsefit.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class BadgeDao_Impl implements BadgeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BadgeEntity> __insertionAdapterOfBadgeEntity;

  public BadgeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBadgeEntity = new EntityInsertionAdapter<BadgeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `badges` (`badgeId`,`name`,`description`,`earned`,`earnedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BadgeEntity entity) {
        statement.bindString(1, entity.getBadgeId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        final int _tmp = entity.getEarned() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getEarnedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getEarnedAt());
        }
      }
    };
  }

  @Override
  public Object insertAll(final List<BadgeEntity> badges,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBadgeEntity.insert(badges);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BadgeEntity>> getBadges() {
    final String _sql = "SELECT * FROM badges";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"badges"}, new Callable<List<BadgeEntity>>() {
      @Override
      @NonNull
      public List<BadgeEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBadgeId = CursorUtil.getColumnIndexOrThrow(_cursor, "badgeId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "earned");
          final int _cursorIndexOfEarnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "earnedAt");
          final List<BadgeEntity> _result = new ArrayList<BadgeEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BadgeEntity _item;
            final String _tmpBadgeId;
            _tmpBadgeId = _cursor.getString(_cursorIndexOfBadgeId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final boolean _tmpEarned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEarned);
            _tmpEarned = _tmp != 0;
            final Long _tmpEarnedAt;
            if (_cursor.isNull(_cursorIndexOfEarnedAt)) {
              _tmpEarnedAt = null;
            } else {
              _tmpEarnedAt = _cursor.getLong(_cursorIndexOfEarnedAt);
            }
            _item = new BadgeEntity(_tmpBadgeId,_tmpName,_tmpDescription,_tmpEarned,_tmpEarnedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
