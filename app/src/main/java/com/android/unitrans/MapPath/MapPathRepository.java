package com.android.unitrans.MapPath;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MapPathRepository {

    private MapPathDao dao;
    private LiveData<List<MapPath>> mapPaths;

    public MapPathRepository(Application application)
    {
        MapPathDb db = MapPathDb.getBudgetDb(application);
        dao = db.mapPathDao();
        mapPaths = dao.getLiveMapPath();
    }

    public LiveData<List<MapPath>> getLiveMapPath() {
        return mapPaths;
    }

    public List<MapPath> getMapPath() {
        return dao.getMapPath();
    }

    public List<MapPath> getMapPath(String name) {
        return dao.getMapPath(name);
    }

    public void insert(MapPath mapPath)
    {
        new InsertAsyncTask(dao).execute(mapPath);
    }

    public void delete(MapPath mapPath)
    {
        new DeleteAsyncTask(dao).execute(mapPath);
    }

    public void update(MapPath mapPath)
    {
        new UpdateAsyncTask(dao).execute(mapPath);
    }

    private static class InsertAsyncTask extends AsyncTask<MapPath, Void, Void>
    {
        private MapPathDao mAsyncTaskDao;

        InsertAsyncTask(MapPathDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MapPath... mapPaths) {
            mAsyncTaskDao.insert(mapPaths[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<MapPath, Void, Void>
    {
        private MapPathDao mAsyncTaskDao;

        DeleteAsyncTask(MapPathDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MapPath... mapPaths) {
            mAsyncTaskDao.delete(mapPaths[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<MapPath, Void, Void>
    {
        private MapPathDao mAsyncTaskDao;

        UpdateAsyncTask(MapPathDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(MapPath... mapPaths) {
            mAsyncTaskDao.update(mapPaths[0]);
            return null;
        }
    }
}
