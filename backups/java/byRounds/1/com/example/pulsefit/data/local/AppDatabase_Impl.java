package com.example.pulsefit.data.local;

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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ActivityDao _activityDao;

  private volatile ProfileDao _profileDao;

  private volatile SettingsDao _settingsDao;

  private volatile BadgeDao _badgeDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `activities` (`localId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `durationSeconds` INTEGER NOT NULL, `distanceKm` REAL NOT NULL, `calories` INTEGER NOT NULL, `notes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `syncStatus` TEXT NOT NULL, PRIMARY KEY(`localId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `profile` (`userId` TEXT NOT NULL, `username` TEXT NOT NULL, `email` TEXT NOT NULL, `xp` INTEGER NOT NULL, `tier` TEXT NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `settings` (`userId` TEXT NOT NULL, `units` TEXT NOT NULL, `theme` TEXT NOT NULL, `language` TEXT NOT NULL, `notificationsEnabled` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `badges` (`badgeId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `earned` INTEGER NOT NULL, `earnedAt` INTEGER, PRIMARY KEY(`badgeId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'aa6f38832cb14afbea4b6ebb6bfdf7ef')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `activities`");
        db.execSQL("DROP TABLE IF EXISTS `profile`");
        db.execSQL("DROP TABLE IF EXISTS `settings`");
        db.execSQL("DROP TABLE IF EXISTS `badges`");
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
        final HashMap<String, TableInfo.Column> _columnsActivities = new HashMap<String, TableInfo.Column>(9);
        _columnsActivities.put("localId", new TableInfo.Column("localId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("durationSeconds", new TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("distanceKm", new TableInfo.Column("distanceKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("calories", new TableInfo.Column("calories", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivities.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivities = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActivities = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActivities = new TableInfo("activities", _columnsActivities, _foreignKeysActivities, _indicesActivities);
        final TableInfo _existingActivities = TableInfo.read(db, "activities");
        if (!_infoActivities.equals(_existingActivities)) {
          return new RoomOpenHelper.ValidationResult(false, "activities(com.example.pulsefit.data.local.ActivityEntity).\n"
                  + " Expected:\n" + _infoActivities + "\n"
                  + " Found:\n" + _existingActivities);
        }
        final HashMap<String, TableInfo.Column> _columnsProfile = new HashMap<String, TableInfo.Column>(5);
        _columnsProfile.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfile.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfile.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfile.put("xp", new TableInfo.Column("xp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProfile.put("tier", new TableInfo.Column("tier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProfile = new TableInfo("profile", _columnsProfile, _foreignKeysProfile, _indicesProfile);
        final TableInfo _existingProfile = TableInfo.read(db, "profile");
        if (!_infoProfile.equals(_existingProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "profile(com.example.pulsefit.data.local.ProfileEntity).\n"
                  + " Expected:\n" + _infoProfile + "\n"
                  + " Found:\n" + _existingProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsSettings = new HashMap<String, TableInfo.Column>(5);
        _columnsSettings.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSettings.put("units", new TableInfo.Column("units", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSettings.put("theme", new TableInfo.Column("theme", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSettings.put("language", new TableInfo.Column("language", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSettings.put("notificationsEnabled", new TableInfo.Column("notificationsEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSettings = new TableInfo("settings", _columnsSettings, _foreignKeysSettings, _indicesSettings);
        final TableInfo _existingSettings = TableInfo.read(db, "settings");
        if (!_infoSettings.equals(_existingSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "settings(com.example.pulsefit.data.local.SettingsEntity).\n"
                  + " Expected:\n" + _infoSettings + "\n"
                  + " Found:\n" + _existingSettings);
        }
        final HashMap<String, TableInfo.Column> _columnsBadges = new HashMap<String, TableInfo.Column>(5);
        _columnsBadges.put("badgeId", new TableInfo.Column("badgeId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("earned", new TableInfo.Column("earned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBadges.put("earnedAt", new TableInfo.Column("earnedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBadges = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBadges = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBadges = new TableInfo("badges", _columnsBadges, _foreignKeysBadges, _indicesBadges);
        final TableInfo _existingBadges = TableInfo.read(db, "badges");
        if (!_infoBadges.equals(_existingBadges)) {
          return new RoomOpenHelper.ValidationResult(false, "badges(com.example.pulsefit.data.local.BadgeEntity).\n"
                  + " Expected:\n" + _infoBadges + "\n"
                  + " Found:\n" + _existingBadges);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "aa6f38832cb14afbea4b6ebb6bfdf7ef", "852385ed74aa0fb79969133bc3438b8e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "activities","profile","settings","badges");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `activities`");
      _db.execSQL("DELETE FROM `profile`");
      _db.execSQL("DELETE FROM `settings`");
      _db.execSQL("DELETE FROM `badges`");
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
    _typeConvertersMap.put(ActivityDao.class, ActivityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProfileDao.class, ProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SettingsDao.class, SettingsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BadgeDao.class, BadgeDao_Impl.getRequiredConverters());
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
  public ActivityDao activityDao() {
    if (_activityDao != null) {
      return _activityDao;
    } else {
      synchronized(this) {
        if(_activityDao == null) {
          _activityDao = new ActivityDao_Impl(this);
        }
        return _activityDao;
      }
    }
  }

  @Override
  public ProfileDao profileDao() {
    if (_profileDao != null) {
      return _profileDao;
    } else {
      synchronized(this) {
        if(_profileDao == null) {
          _profileDao = new ProfileDao_Impl(this);
        }
        return _profileDao;
      }
    }
  }

  @Override
  public SettingsDao settingsDao() {
    if (_settingsDao != null) {
      return _settingsDao;
    } else {
      synchronized(this) {
        if(_settingsDao == null) {
          _settingsDao = new SettingsDao_Impl(this);
        }
        return _settingsDao;
      }
    }
  }

  @Override
  public BadgeDao badgeDao() {
    if (_badgeDao != null) {
      return _badgeDao;
    } else {
      synchronized(this) {
        if(_badgeDao == null) {
          _badgeDao = new BadgeDao_Impl(this);
        }
        return _badgeDao;
      }
    }
  }
}
