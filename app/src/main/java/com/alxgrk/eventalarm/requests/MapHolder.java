/*
 * Created on 03.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.activities.home.maps.ArtistMap;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.activities.home.maps.ImageMap;
import com.alxgrk.eventalarm.util.BitmapUtil;
import com.alxgrk.eventalarm.util.NullChecker;
import com.alxgrk.eventalarm.util.ParcelUtil;

public class MapHolder implements Parcelable {

    public static final int TYPE_MAPHOLDER = 111;

    @Override
    public int describeContents() {
        return TYPE_MAPHOLDER;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtil util = new ParcelUtil(out);

        util.writeBoolean(allRequestsFinished) //
                .writeBoolean(artistMap.ready())//
                .writeBoolean(eventMap.ready()) //
                .writeBoolean(imageMap.ready()) //
                .writeBoolean(blurredImageMap.ready()) //
                .writeMap(artistMap.getMap()) //
                .writeMap(eventMap.getMap()) //
                .writeMap(imageMap.getMap()) //
                .writeMap(blurredImageMap.getMap());
    }

    public static final Parcelable.Creator<MapHolder> CREATOR = new Parcelable.Creator<MapHolder>() {

        @Override
        public MapHolder createFromParcel(Parcel in) {
            return new MapHolder(in);
        }

        @Override
        public MapHolder[] newArray(int size) {
            return new MapHolder[size];
        }

    };

    private ArtistMap artistMap;

    private EventMap eventMap;

    private ImageMap imageMap;

    private ImageMap blurredImageMap;

    boolean allRequestsFinished = false;

    public MapHolder() {
        this(new ArtistMap(), new EventMap(), new ImageMap());
    }

    protected MapHolder(Parcel in) {
        this();
        ParcelUtil util = new ParcelUtil(in);

        allRequestsFinished = util.readBoolean();

        artistMap.setReady(util.readBoolean());
        eventMap.setReady(util.readBoolean());
        imageMap.setReady(util.readBoolean());
        blurredImageMap.setReady(util.readBoolean());

        artistMap.setMap(util.readArtistMap());
        eventMap.setMap(util.readEventMap());
        imageMap.setMap(util.readImageMap());
        blurredImageMap.setMap(util.readImageMap());
    }

    public MapHolder(@NonNull ArtistMap artistMap, @NonNull EventMap eventMap,
            @NonNull ImageMap imageMap) {
        NullChecker.checkNull(artistMap);
        NullChecker.checkNull(eventMap);
        NullChecker.checkNull(imageMap);

        this.artistMap = artistMap;
        this.eventMap = eventMap;
        this.imageMap = imageMap;

        this.blurredImageMap = imageMap;

        ready();
    }

    public boolean ready() {
        return allRequestsFinished = artistMap.ready() && eventMap.ready() && imageMap.ready();
    }

    public ArtistMap getArtistMap() {
        return artistMap;
    }

    public EventMap getEventMap() {
        return eventMap;
    }

    public ImageMap getImageMap() {
        return imageMap;
    }

    public ImageMap getBlurredImageMap() {
        return blurredImageMap;
    }

    public MapHolder setArtistMap(@NonNull ArtistMap artistMap) {
        NullChecker.checkNull(artistMap);

        this.artistMap = artistMap;
        return this;
    }

    public MapHolder setEventMap(@NonNull EventMap eventMap) {
        NullChecker.checkNull(eventMap);

        this.eventMap = eventMap;
        return this;
    }

    public MapHolder setImageMap(@NonNull ImageMap imageMap) {
        NullChecker.checkNull(imageMap);

        this.imageMap = imageMap;
        return this;
    }

    public MapHolder setBlurredImageMap(@NonNull ImageMap blurredImageMap) {
        NullChecker.checkNull(blurredImageMap);

        this.blurredImageMap = blurredImageMap;
        return this;
    }

    public MapHolder generateBlurredImages(@NonNull Context context) {
        blurredImageMap = new BitmapUtil().createScaledBlurredShadedVersionOf(imageMap, context);
        blurredImageMap.setReady(true);
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allRequestsFinished ? 1231 : 1237);
        result = prime * result + ((artistMap == null) ? 0 : artistMap.hashCode());
        result = prime * result + ((eventMap == null) ? 0 : eventMap.hashCode());
        result = prime * result + ((imageMap == null) ? 0 : imageMap.hashCode());
        result = prime * result + ((blurredImageMap == null) ? 0 : blurredImageMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MapHolder))
            return false;
        MapHolder other = (MapHolder) obj;
        if (allRequestsFinished != other.allRequestsFinished)
            return false;
        if (artistMap == null) {
            if (other.artistMap != null)
                return false;
        } else if (!artistMap.equals(other.artistMap))
            return false;
        if (eventMap == null) {
            if (other.eventMap != null)
                return false;
        } else if (!eventMap.equals(other.eventMap))
            return false;
        if (imageMap == null) {
            if (other.imageMap != null)
                return false;
        } else if (!imageMap.equals(other.imageMap))
            return false;
        return true;
    }

    /**
     * NOT GENERATED!
     */
    @Override
    public String toString() {
        String artistString = artistMap.toString();
        String eventString = eventMap.toString();
        String imageString = imageMap.toString();

        String subArtistString = artistString.length() >= 50 ? artistString.substring(0, 50)
                : artistString;
        String subEventString = eventString.length() >= 50 ? eventString.substring(0, 50)
                : eventString;
        String subImageString = imageString.length() >= 50 ? imageString.substring(0, 50)
                : imageString;

        return "MapHolder [" + subArtistString + ", " + subEventString + ", " + subImageString
                + ", allRequestsFinished=" + allRequestsFinished + "]";
    }
}
