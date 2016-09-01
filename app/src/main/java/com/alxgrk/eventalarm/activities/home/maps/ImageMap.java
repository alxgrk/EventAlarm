/*
 * Created on 31.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.maps;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.NullChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageMap implements RequestMap<String, Bitmap> {

    private Map<String, Bitmap> imageMap;

    private boolean isImagesAvailable;

    public ImageMap() {
        imageMap = new HashMap<>();
        isImagesAvailable = false;
    }

    public ImageMap(@NonNull ImageMap clone) {
        NullChecker.checkNull(clone);

        this.imageMap = new HashMap<>(clone.getMap());
        this.isImagesAvailable = clone.ready();
    }

    @Override
    public final void requestFinished(@NonNull Map<String, Bitmap> imageMap) {
        NullChecker.checkNull(imageMap);

        setMap(imageMap);
        isImagesAvailable = true;
    }

    @Override
    public final void setMap(@NonNull Map<String, Bitmap> imageMap) {
        NullChecker.checkNull(imageMap);

        this.imageMap = imageMap;
    }

    @Override
    public final @NonNull Map<String, Bitmap> getMap() {
        return new HashMap<>(imageMap);
    }

    @Override
    public final boolean ready() {
        return isImagesAvailable;
    }

    @Override
    public final void setReady(boolean b) {
        isImagesAvailable = b;
    }

    @Override
    public final @NonNull Bitmap get(@NonNull String key) {
        NullChecker.checkNull(key);

        return imageMap.containsKey(key) ? imageMap.get(key) : BitmapUtil.EMPTY_BITMAP;
    }

    @Override
    public final Bitmap put(@NonNull String key, @NonNull Bitmap value) {
        NullChecker.checkNull(key);
        NullChecker.checkNull(value);

        return imageMap.put(key, value);
    }

    @Override
    public final @NonNull Set<String> keySet() {
        return imageMap.keySet();
    }

    // NOT GENERATED!!!
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageMap == null) ? 0 : imageMap.keySet().hashCode());
        result = prime * result + (isImagesAvailable ? 1231 : 1237);
        return result;
    }

    // NOT GENERATED!!!
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ImageMap))
            return false;
        ImageMap other = (ImageMap) obj;
        if (imageMap == null) {
            if (other.imageMap != null)
                return false;
        } else if (!imageMap.keySet().equals(other.imageMap.keySet()))
            return false;
        if (isImagesAvailable != other.isImagesAvailable)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ImageMap [imageMap=" + imageMap + ", isImagesAvailable=" + isImagesAvailable + "]";
    }
}
