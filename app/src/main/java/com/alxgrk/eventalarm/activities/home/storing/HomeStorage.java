/*
 * Created on 31.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.storing;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.datastores.DataStoreExternal;
import com.alxgrk.eventalarm.util.datastores.RecoveryException;

import static com.alxgrk.eventalarm.util.Constants.HOME_STORAGE_TAG;

class HomeStorage {

    protected HomeActivity home;

    protected DataStoreExternal storage;

    protected MapHolder latestMapHolder;

    protected HomeStorage(@NonNull HomeActivity home, @NonNull DataStoreExternal storage) {
        NullChecker.checkNull(home);
        NullChecker.checkNull(storage);

        this.storage = storage;
        this.home = home;
        latestMapHolder = home.getMapHolder(); 
        if (!latestMapHolder.ready())
            latestMapHolder = recover();
    }

    protected MapHolder recover() {
        MapHolder mapHolder;
        try {
            mapHolder = storage.getMapHolder();
            mapHolder.getArtistMap().setReady(storage.getBoolean("isArtistAvailable"));
            mapHolder.getEventMap().setReady(storage.getBoolean("isEventsAvailable"));
            mapHolder.getImageMap().setReady(storage.getBoolean("isImagesAvailable"));
            mapHolder.ready();
        } catch (RecoveryException e) {
            Log.e(HOME_STORAGE_TAG, e.toString());
            mapHolder = new MapHolder();
        }
        return mapHolder;
    }

    protected void save() {
        if (!home.getMapHolder().equals(latestMapHolder)) {
            latestMapHolder = home.getMapHolder();
            SaverTask saverTask = new SaverTask(latestMapHolder);
            saverTask.execute();
        }
    }

    private class SaverTask extends AsyncTask<Void, Void, Void> {

        private MapHolder mapHolder;

        private SaverTask(final MapHolder mapHolder) {
            this.mapHolder = mapHolder;
        }

        @Override
        protected Void doInBackground(Void... params) {
            saveDataOnStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("SaverTask", "finished");
        }

        private void saveDataOnStorage() {
            storage //
                    .putBoolean("isArtistAvailable", mapHolder.getArtistMap().ready()) //
                    .putBoolean("isEventsAvailable", mapHolder.getEventMap().ready()) //
                    .putBoolean("isImagesAvailable", mapHolder.getImageMap().ready()) //
                    .putMapHolder(mapHolder);
        }
    }
}
