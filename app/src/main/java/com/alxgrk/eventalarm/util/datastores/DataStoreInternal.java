/*
 * Created on 23.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util.datastores;

import android.graphics.Bitmap;

import com.alxgrk.eventalarm.info.InfoObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DataStoreInternal {

    public enum Suffix {
        SUFFIX_JPG(".jpg"), SUFFIX_BLURRED_JPG("_b.jpg");

        private String suffix;

        private Suffix(String suffix) {
            this.suffix = suffix;
        }

        public String getSuffix() {
            return suffix;
        }
    }

    // getter/setter ArtistMap (only intern)

    abstract DataStoreInternal setArtistMap(Map<String, String> artistMap);

    abstract Map<String, String> getArtistMap() throws RecoveryException;

    // getter/setter EventMap (only intern)

    abstract DataStoreInternal setEventMap(Map<String, List<InfoObject>> eventMap);

    abstract Map<String, List<InfoObject>> getEventMap() throws RecoveryException;

    // getter/setter ImageMap (only intern)

    abstract DataStoreInternal setImageMap(Map<String, Bitmap> imageMap, Suffix suffix);

    abstract Map<String, Bitmap> getImageMap(Set<String> artists, Suffix suffix)
            throws RecoveryException;
}
