package com.android.unitrans.MapPath;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {MapPath.class}, version = 1, exportSchema = false)
public abstract class MapPathDb extends RoomDatabase {

    public abstract MapPathDao mapPathDao();

    private static volatile MapPathDb mapPathDb;

    public static MapPathDb getBudgetDb(Context context)
    {
        if (mapPathDb == null)
        {
            synchronized (MapPathDb.class)
            {
                if (mapPathDb == null)
                    mapPathDb = Room.databaseBuilder(context, MapPathDb.class, "map_path_db").build();
            }
        }

        return mapPathDb;
    }
}
