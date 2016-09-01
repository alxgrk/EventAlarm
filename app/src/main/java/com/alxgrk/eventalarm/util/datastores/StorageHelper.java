/*
 * Created on 22.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util.datastores;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.info.InfoObject;
import com.alxgrk.eventalarm.requests.MapHolder;
import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.NullChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alxgrk.eventalarm.util.Constants.*;

public class StorageHelper extends DataStoreInternal implements DataStoreExternal {

    private static StorageHelper INSTANCE = null;

    private Application application = null;

    private SharedPreferences sharedPref = null;

    private SharedPreferences.Editor sharedEditor = null;

    private static final String ARTISTS_FILE = "artists.map";

    private static final String EVENTS_FILE = "events.map";

    private StorageHelper(Application application) {
        this.application = application;
        sharedPref = this.application.getSharedPreferences("main", Context.MODE_PRIVATE);
        sharedEditor = sharedPref.edit();
    }

    public static StorageHelper getInstance(@NonNull Application application) {
        NullChecker.checkNull(application);

        if (INSTANCE == null) {
            INSTANCE = new StorageHelper(application);
        }
        return INSTANCE;
    }

    public void clear() {
        sharedEditor.clear().commit();
    }

    @Override
    public StorageHelper putInteger(@NonNull String name, int value) {
        NullChecker.checkNull(name);

        sharedEditor.putInt(name, value).commit();
        return INSTANCE;
    }

    @Override
    public int getInteger(@NonNull String name) {
        NullChecker.checkNull(name);

        return sharedPref.getInt(name, 0);
    }

    @Override
    public StorageHelper putString(@NonNull String name, @NonNull String value) {
        NullChecker.checkNull(name);
        NullChecker.checkNull(value);

        sharedEditor.putString(name, value).commit();
        return INSTANCE;
    }

    @Override
    public @NonNull String getString(@NonNull String name) {
        NullChecker.checkNull(name);

        return sharedPref.getString(name, "");
    }

    @Override
    public StorageHelper putLong(@NonNull String name, long value) {
        NullChecker.checkNull(name);

        sharedEditor.putLong(name, value).commit();
        return INSTANCE;
    }

    @Override
    public long getLong(@NonNull String name) {
        return sharedPref.getLong(name, 0);
    }

    @Override
    public StorageHelper putBoolean(@NonNull String name, boolean value) {
        NullChecker.checkNull(name);

        sharedEditor.putBoolean(name, value).commit();
        return INSTANCE;
    }

    @Override
    public boolean getBoolean(@NonNull String name) {
        NullChecker.checkNull(name);

        return sharedPref.getBoolean(name, false);
    }

    @Override
    public StorageHelper putMapHolder(@NonNull MapHolder mapHolder) {
        NullChecker.checkNull(mapHolder);

        setArtistMap(mapHolder.getArtistMap().getMap());
        setEventMap(mapHolder.getEventMap().getMap());
        setImageMap(mapHolder.getImageMap().getMap(), Suffix.SUFFIX_JPG);
        setImageMap(mapHolder.getBlurredImageMap().getMap(), Suffix.SUFFIX_BLURRED_JPG);

        Log.d(STORAGE_HELPER_TAG, "persisted mapHolder successfully");
        return INSTANCE;
    }

    @Override
    public @NonNull MapHolder getMapHolder() throws RecoveryException {
        ArtistMap artistMap = new ArtistMap();
        artistMap.setMap(getArtistMap());

        EventMap eventMap = new EventMap();
        eventMap.setMap(getEventMap());

        ImageMap imageMap = new ImageMap();
        imageMap.setMap(getImageMap(artistMap.keySet(), Suffix.SUFFIX_JPG));

        ImageMap blurredImageMap = new ImageMap();
        blurredImageMap.setMap(getImageMap(artistMap.keySet(), Suffix.SUFFIX_BLURRED_JPG));

        MapHolder mapHolder = new MapHolder(artistMap, eventMap, imageMap);
        mapHolder.setBlurredImageMap(blurredImageMap);

        Log.d(STORAGE_HELPER_TAG, "read mapHolder successfully");

        return mapHolder;
    }

    // --------------- INTERNALS ---------------

    @Override
    StorageHelper setArtistMap(@NonNull Map<String, String> artistMap) {
        NullChecker.checkNull(artistMap);

        try {
            FileOutputStream fos = application.getApplicationContext().openFileOutput(ARTISTS_FILE,
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(artistMap);

            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e(STORAGE_HELPER_TAG, e.toString());
        }

        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    Map<String, String> getArtistMap() throws RecoveryException {
        Map<String, String> artistMap = new HashMap<String, String>();

        try {
            FileInputStream fis = application.getApplicationContext().openFileInput(ARTISTS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);

            artistMap = (Map<String, String>) ois.readObject();

            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.d(STORAGE_HELPER_TAG, e.toString());
            throw new RecoveryException("ArtistMap");
        }

        return artistMap;
    }

    @Override
    StorageHelper setEventMap(@NonNull Map<String, List<InfoObject>> eventMap) {
        NullChecker.checkNull(eventMap);

        try {
            FileOutputStream fos = application.getApplicationContext().openFileOutput(EVENTS_FILE,
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(eventMap);

            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e(STORAGE_HELPER_TAG, e.toString());
        }

        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    Map<String, List<InfoObject>> getEventMap() throws RecoveryException {
        Map<String, List<InfoObject>> eventMap = new HashMap<>();

        try {
            FileInputStream fis = application.getApplicationContext().openFileInput(EVENTS_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);

            eventMap = (Map<String, List<InfoObject>>) ois.readObject();

            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.d(STORAGE_HELPER_TAG, e.toString());
            throw new RecoveryException("EventMap");
        }

        return eventMap;
    }

    @Override
    StorageHelper setImageMap(@NonNull Map<String, Bitmap> imageMap, @NonNull Suffix suffix) {
        NullChecker.checkNull(imageMap);

        ContextWrapper cw = new ContextWrapper(application.getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        for (String artist : imageMap.keySet()) {
            File picture = new File(directory, eliminateForbiddenChars(artist) + suffix
                    .getSuffix());
            try {
                FileOutputStream fos = new FileOutputStream(picture);
                imageMap.get(artist).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                Log.e(STORAGE_HELPER_TAG, e.toString());
            }
        }

        return INSTANCE;
    }

    @Override
    @NonNull
    Map<String, Bitmap> getImageMap(@NonNull Set<String> artists, @NonNull Suffix suffix)
            throws RecoveryException {
        NullChecker.checkNull(artists);

        Map<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
        BitmapUtil bitmapUtil = new BitmapUtil();

        ContextWrapper cw = new ContextWrapper(application.getApplicationContext());
        Resources resources = application.getResources();
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        for (String artist : artists) {
            File picture = new File(directory, eliminateForbiddenChars(artist) + suffix
                    .getSuffix());

            // FIXME do not reload images if they are already in use!
            if (picture.exists()) {
                try {
                    // TODO change required size
                    Bitmap bitmap = bitmapUtil.loadSampledBitmapFrom(picture, 200, 200);
                    imageMap.put(artist, bitmapUtil.scaleBitmap(bitmap));
                } catch (Exception e) {
                    Log.d(STORAGE_HELPER_TAG, e.toString());
                    throw new RecoveryException("ImageMap");
                }
            } else {
                bitmapUtil.fillDefault(artist, imageMap, resources);
            }
        }
        Log.d(STORAGE_HELPER_TAG, suffix.getSuffix() + " image map successfully recovered");

        return imageMap;
    }

    private String eliminateForbiddenChars(String s) {
        return s.replace("*", "").replace(".", "").replace("\"", "").replace("/", "").replace("\\",
                "").replace("[", "").replace("]", "").replace(":", "").replace(";", "").replace("|",
                        "").replace("=", "").replace(",", "");
    }
}
