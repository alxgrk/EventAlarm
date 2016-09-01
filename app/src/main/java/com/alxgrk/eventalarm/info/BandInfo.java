/*
 * Created on 26.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.info;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.util.NullChecker;

public class BandInfo implements InfoObject {

    private String bandName;

    private Bitmap bandImage;

    public BandInfo(@NonNull String bandName, @NonNull Bitmap bandImage) {
        NullChecker.checkNull(bandName);
        NullChecker.checkNull(bandImage);

        this.bandName = bandName;
        this.bandImage = bandImage;
    }

    public String getBandName() {
        return bandName;
    }

    public Bitmap getBandImage() {
        return bandImage;
    }

    @Override
    public String toString() {
        return "BandInfo [bandName=" + bandName + ", bandImage=" + bandImage + "]";
    }

    @Override
    public Class<BandInfo> getRuntimeType() {
        return BandInfo.class;
    }

    @Override
    public int compareTo(@NonNull InfoObject another) {
        NullChecker.checkNull(another);

        if (another.getRuntimeType().equals(getClass())) {
            return bandName.compareTo(((BandInfo) another).getBandName());
        } else {
            return 0; // don't care about order
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bandName == null) ? 0 : bandName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BandInfo))
            return false;
        BandInfo other = (BandInfo) obj;
        if (bandName == null) {
            if (other.bandName != null)
                return false;
        } else if (!bandName.equals(other.bandName))
            return false;
        return true;
    }
}
