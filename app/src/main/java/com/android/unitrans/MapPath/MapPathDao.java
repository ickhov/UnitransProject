package com.android.unitrans.MapPath;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MapPathDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MapPath mapPath);

    @Delete
    void delete(MapPath mapPath);

    @Update
    void update(MapPath mapPath);

    @Query("SELECT * FROM `mappath` ORDER BY id ASC")
    LiveData<List<MapPath>> getLiveMapPath();

    @Query("SELECT * FROM `mappath` ORDER BY id ASC")
    List<MapPath> getMapPath();

    @Query("SELECT * FROM `mappath` WHERE name LIKE :name ORDER BY id ASC")
    List<MapPath> getMapPath(String name);
}
