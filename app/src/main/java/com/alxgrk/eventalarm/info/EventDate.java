package com.alxgrk.eventalarm.info;

import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.util.NullChecker;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class EventDate implements Serializable, Comparable<EventDate> {

    private static final long serialVersionUID = -58300388861604647L;

    private Date date;

    public EventDate(@NonNull Date date) {
        NullChecker.checkNull(date);

        this.date = date;
    }

    public Date getOriginalDate() {
        return date;
    }

    @Override
    public String toString() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return df.format(date);
    }

    public boolean after(@NonNull EventDate thatDate) {
        NullChecker.checkNull(thatDate);

        return date.after(thatDate.getOriginalDate());
    }

    public boolean before(@NonNull EventDate thatDate) {
        NullChecker.checkNull(thatDate);
        return date.before(thatDate.getOriginalDate());
    }

    @Override
    public int compareTo(@NonNull EventDate thatDate) {
        NullChecker.checkNull(thatDate);

        return date.compareTo(thatDate.getOriginalDate());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EventDate))
            return false;
        EventDate other = (EventDate) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }
}