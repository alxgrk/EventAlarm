/*
 * Created on 26.01.2016
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;

import com.alxgrk.eventalarm.info.BandInfo;
import com.alxgrk.eventalarm.info.EventDetails;
import com.alxgrk.eventalarm.info.InfoObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParcelUtil {

    private Parcel parcel;

    public ParcelUtil(Parcel parcel) {
        this.parcel = parcel;
    }

    public ParcelUtil writeBoolean(boolean value) {
        parcel.writeByte((byte) (value ? 1 : 0));
        return this;
    }

    public boolean readBoolean() {
        return parcel.readByte() != 0;
    }

    public <T> ParcelUtil writeMap(Map<String, T> map) throws IllegalArgumentException {
        Bundle b = new Bundle();

        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();

            if (value instanceof String) {
                b.putString(key, (String) value);
            } else if (value instanceof List) {
                @SuppressWarnings("rawtypes")
                List valueList = (List) value;
                Bundle listBundle = new Bundle();

                for (Object obj : valueList) {
                    if (obj instanceof EventDetails) {
                        EventDetails event = (EventDetails) obj;
                        // event.getSwURL() is identifying
                        listBundle.putSerializable(event.getSwURL(), event);
                    } else if (obj instanceof BandInfo) {
                        BandInfo band = (BandInfo) obj;
                        String bandName = band.getBandName();

                        Bundle bandInfo = new Bundle();
                        bandInfo.putString("name", bandName);
                        bandInfo.putParcelable("image", band.getBandImage());

                        listBundle.putBundle(bandName, bandInfo);
                    } else {
                        throw new IllegalArgumentException(
                                "The List you're trying to insert does not contain InfoObjects!");
                    }
                }

                b.putParcelable(key, listBundle);
            } else if (value instanceof Bitmap) {
                b.putParcelable(key, (Bitmap) value);
            } else {
                throw new IllegalArgumentException("The value-type " + value.getClass()
                        + " is not supported! See docs for a list of supported types.");
            }
        }

        parcel.writeBundle(b);
        return this;
    }

    public Map<String, String> readArtistMap() throws IllegalArgumentException {
        Bundle b = parcel.readBundle();
        Map<String, String> map = new HashMap<>();

        for (String key : b.keySet()) {
            Object value = b.get(key);

            if (value instanceof String) {
                map.put(key, (String) value);
            } else {
                throw new IllegalArgumentException(
                        "Possibly the wrong order to ask for Parcel content! See Parcelable docs.");
            }

        }

        return map;
    }

    public Map<String, List<InfoObject>> readEventMap() throws IllegalArgumentException {
        Bundle b = parcel.readBundle();
        Map<String, List<InfoObject>> map = new HashMap<>();

        for (String key : b.keySet()) {
            Object value = b.get(key);

            if (value instanceof Bundle) {
                @SuppressWarnings("rawtypes")
                Bundle listBundle = (Bundle) value;
                List<InfoObject> infoList = new LinkedList<>();

                for (String listKey : listBundle.keySet()) {
                    Object listValue = listBundle.get(listKey);

                    if (listValue instanceof EventDetails) {
                        infoList.add((InfoObject) listValue);
                    } else if (listValue instanceof Bundle) {
                        Bundle bandBundle = (Bundle) listValue;
                        String bandName = bandBundle.getString("name");
                        Bitmap bandImage = bandBundle.getParcelable("image");

                        infoList.add(new BandInfo(bandName, bandImage));
                    } else {
                        throw new IllegalArgumentException(
                                "The right order to ask for, but these are no InfoObjects in the List! ("
                                        + infoList + ")");
                    }
                }

                map.put(key, infoList);
            } else {
                throw new IllegalArgumentException(
                        "Possibly the wrong order to ask for Parcel content! See Parcelable docs.");
            }

        }

        return map;
    }

    public Map<String, Bitmap> readImageMap() throws IllegalArgumentException {
        Bundle b = parcel.readBundle();
        Map<String, Bitmap> map = new HashMap<>();

        for (String key : b.keySet()) {
            Object value = b.get(key);

            if (value instanceof Bitmap) {
                map.put(key, (Bitmap) value);
            } else {
                throw new IllegalArgumentException(
                        "Possibly the wrong order to ask for Parcel content! See Parcelable docs.");
            }

        }

        return map;
    }
}
