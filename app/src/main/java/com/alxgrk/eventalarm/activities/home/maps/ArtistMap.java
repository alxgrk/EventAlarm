/*
 * Created on 31.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.maps;

import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.util.NullChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ArtistMap implements RequestMap<String, String> {

    private Map<String, String> artistMap;

    private boolean isArtistsAvailable = false;

    public ArtistMap() {
        artistMap = new HashMap<>();
        isArtistsAvailable = false;
    }

    public ArtistMap(@NonNull ArtistMap clone) {
        NullChecker.checkNull(clone);

        this.artistMap = new HashMap<>(clone.getMap());
        this.isArtistsAvailable = clone.ready();
    }

    @Override
    public final void requestFinished(@NonNull Map<String, String> artistMap) {
        NullChecker.checkNull(artistMap);

        setMap(artistMap);
        isArtistsAvailable = true;
    }

    @Override
    public final void setMap(@NonNull Map<String, String> artistMap) {
        NullChecker.checkNull(artistMap);

        this.artistMap = artistMap;
    }

    @Override
    public final @NonNull Map<String, String> getMap() {
        return new HashMap<>(artistMap);
    }

    @Override
    public final boolean ready() {
        return isArtistsAvailable;
    }

    @Override
    public final void setReady(boolean b) {
        isArtistsAvailable = b;
    }

    @Override
    public final @NonNull String get(@NonNull String key) {
        NullChecker.checkNull(key);

        return artistMap.containsKey(key) ? artistMap.get(key) : "";
    }

    @Override
    public final @NonNull String put(@NonNull String key, @NonNull String value) {
        NullChecker.checkNull(key);
        NullChecker.checkNull(value);

        return artistMap.put(key, value);
    }

    @Override
    public final @NonNull Set<String> keySet() {
        return artistMap.keySet();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((artistMap == null) ? 0 : artistMap.hashCode());
        result = prime * result + (isArtistsAvailable ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ArtistMap))
            return false;
        ArtistMap other = (ArtistMap) obj;
        if (artistMap == null) {
            if (other.artistMap != null)
                return false;
        } else if (!artistMap.equals(other.artistMap))
            return false;
        if (isArtistsAvailable != other.isArtistsAvailable)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ArtistMap [artistMap=" + artistMap + ", isArtistsAvailable=" + isArtistsAvailable
                + "]";
    }
}
