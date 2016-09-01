/*
 * Created on 24.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.storing;

import android.util.Log;

import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.util.datastores.StorageHelper;

import static com.alxgrk.eventalarm.util.Constants.HOME_STORAGE_TAG;

public class PersistentStorage extends HomeStorage {

    public PersistentStorage(HomeActivity home, StorageHelper storageHelper) {
        super(home, storageHelper);
    }

    public boolean recoverPersistentData() {
        boolean successful;
        try {
            home.setMapHolder(latestMapHolder);

            successful = latestMapHolder.ready();
            Log.d(HOME_STORAGE_TAG, storage.getClass().getSimpleName() + " has ready state "
                    + successful);
        } catch (NullPointerException e) {
            Log.e(HOME_STORAGE_TAG, "No data was saved in persistent storage..");
            successful = false;
        }

        return successful;
    }

    public void savePersistentData() {
        save();
    }
}
