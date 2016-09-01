/*
 * Created on 31.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.home.maps;

import android.support.annotation.NonNull;

import java.util.Map;
import java.util.Set;

public interface RequestMap<K, V> {

    void requestFinished(@NonNull Map<K, V> map);

    void setMap(@NonNull Map<K, V> map);

    @NonNull
    Map<K, V> getMap();

    boolean ready();

    void setReady(boolean b);

    @NonNull
    V get(@NonNull K key);

    @NonNull
    V put(@NonNull K key, @NonNull V value);

    @NonNull
    Set<K> keySet();
}
