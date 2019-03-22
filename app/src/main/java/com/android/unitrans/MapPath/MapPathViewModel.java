package com.android.unitrans.MapPath;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MapPathViewModel extends AndroidViewModel {

    private MapPathRepository repository;
    private LiveData<List<MapPath>> mapPaths;

    public MapPathViewModel(@NonNull Application application) {
        super(application);
        repository = new MapPathRepository(application);
        mapPaths = repository.getLiveMapPath();
    }

    public LiveData<List<MapPath>> getLiveMapPath() {
        return mapPaths;
    }

    public List<MapPath> getMapPath() {
        return repository.getMapPath();
    }

    public List<MapPath> getMapPath(String name) {
        return repository.getMapPath(name);
    }

    public void insert(MapPath mapPath)
    {
        repository.insert(mapPath);
    }

    public void delete(MapPath mapPath)
    {
        repository.delete(mapPath);
    }

    public void update(MapPath mapPath)
    {
        repository.update(mapPath);
    }
}
