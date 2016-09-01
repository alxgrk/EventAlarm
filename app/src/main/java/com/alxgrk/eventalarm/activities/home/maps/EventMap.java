/*
 * Created on 31.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.maps;

import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.info.InfoObject;
import com.alxgrk.eventalarm.util.NullChecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventMap implements RequestMap<String, List<InfoObject>> {

    private Map<String, List<InfoObject>> eventMap;

    private boolean isEventsAvailable;

    public EventMap() {
        eventMap = new HashMap<>();
        isEventsAvailable = false;
    }

    public EventMap(@NonNull EventMap clone) {
        NullChecker.checkNull(clone);

        this.eventMap = new HashMap<>(clone.getMap());
        this.isEventsAvailable = clone.ready();
    }

    @Override
    public final void requestFinished(@NonNull Map<String, List<InfoObject>> eventMap) {
        NullChecker.checkNull(eventMap);

        setMap(eventMap);
        isEventsAvailable = true;
    }

    @Override
    public final void setMap(@NonNull Map<String, List<InfoObject>> eventMap) {
        NullChecker.checkNull(eventMap);

        this.eventMap = eventMap;
    }

    @Override
    public final @NonNull Map<String, List<InfoObject>> getMap() {
        return new HashMap<>(eventMap);
    }

    @Override
    public final boolean ready() {
        return isEventsAvailable;
    }

    @Override
    public final void setReady(boolean b) {
        isEventsAvailable = b;
    }

    @Override
    public final @NonNull List<InfoObject> get(@NonNull String key) {
        NullChecker.checkNull(key);

        return eventMap.containsKey(key) ? eventMap.get(key) : new LinkedList<InfoObject>();
    }

    @Override
    public final @NonNull List<InfoObject> put(@NonNull String key,
            @NonNull List<InfoObject> value) {
        NullChecker.checkNull(key);
        NullChecker.checkNull(value);

        return eventMap.put(key, value);
    }

    @Override
    public final @NonNull Set<String> keySet() {
        return eventMap.keySet();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventMap == null) ? 0 : eventMap.hashCode());
        result = prime * result + (isEventsAvailable ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EventMap))
            return false;
        EventMap other = (EventMap) obj;
        if (eventMap == null) {
            if (other.eventMap != null)
                return false;
        } else if (!eventMap.equals(other.eventMap))
            return false;
        if (isEventsAvailable != other.isEventsAvailable)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EventMap [eventMap=" + eventMap + ", isEventsAvailable=" + isEventsAvailable + "]";
    }
}
