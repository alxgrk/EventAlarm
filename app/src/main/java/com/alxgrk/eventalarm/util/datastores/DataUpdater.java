/*
 * Created on 23.01.2016
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util.datastores;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.info.BandInfo;
import com.alxgrk.eventalarm.info.InfoObject;
import com.alxgrk.eventalarm.util.EventNotificator;
import com.alxgrk.eventalarm.util.NullChecker;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.*;

public class DataUpdater {

    private Application application;

    private StorageHelper storageHelper;

    public DataUpdater(@NonNull Application application) {
        NullChecker.checkNull(application);

        this.application = application;
        storageHelper = StorageHelper.getInstance(application);
    }

    public ArtistMap getArtistMap() {
        ArtistMap artistMap = new ArtistMap();

        try {
            artistMap.setMap(storageHelper.getArtistMap());
            artistMap.setReady(true);
        } catch (RecoveryException e) {
            Log.d(BACKGROUND_SERVICE_TAG, "could not recover artists from persistent storage; "
                    + e);
        }

        return artistMap;
    }

    public void updateArtists(@NonNull ArtistMap artistMap) {
        NullChecker.checkNull(artistMap);

        storageHelper.setArtistMap(artistMap.getMap());
    }

    public void updateEvents(@NonNull EventMap eventMap) {
        NullChecker.checkNull(eventMap);

        storageHelper.setEventMap(eventMap.getMap());
    }

    public void updateImages(@NonNull ImageMap imageMap, @NonNull DataStoreInternal.Suffix suffix) {
        NullChecker.checkNull(imageMap);

        storageHelper.setImageMap(imageMap.getMap(), suffix);
    }

    public void notifyAboutNewEvents(@NonNull EventMap eventMap) {
        NullChecker.checkNull(eventMap);

        EventNotificator notificator = new EventNotificator(application);

        try {
            Map<String, List<InfoObject>> serializedEventMap = storageHelper.getEventMap();
            Map<String, List<InfoObject>> updatedEventMap = eventMap.getMap();

            if (!serializedEventMap.equals(updatedEventMap)) {
                Set<String> artistSet = updatedEventMap.keySet();
                final Map<String, Bitmap> imageMap = storageHelper.getImageMap(artistSet,
                        DataStoreInternal.Suffix.SUFFIX_JPG);

                Set<Entry<String, List<InfoObject>>> updatedEvents = getUpdatedEvents(
                        serializedEventMap, updatedEventMap);

                for (Entry<String, List<InfoObject>> entry : updatedEvents) {
                    String artist = entry.getKey();
                    BandInfo bandInfo = new BandInfo(artist, imageMap.get(artist));

                    notificator.notificate(artist, bandInfo, entry.getValue());
                }
            }
        } catch (RecoveryException e) {
            Log.e(BACKGROUND_SERVICE_TAG, "could not compare events; " + e);
        }
    }

    private Set<Entry<String, List<InfoObject>>> getUpdatedEvents(
            Map<String, List<InfoObject>> oldState, Map<String, List<InfoObject>> newState) {
        Set<Entry<String, List<InfoObject>>> updatedEvents = new HashSet<>();

        for (Entry<String, List<InfoObject>> entry : newState.entrySet()) {
            if (!oldState.entrySet().contains(entry)) {
                updatedEvents.add(entry);
            }
        }

        return updatedEvents;
    }
}
