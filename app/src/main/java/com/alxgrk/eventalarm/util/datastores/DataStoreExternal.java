/*
 * Created on 26.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util.datastores;

import com.alxgrk.eventalarm.requests.MapHolder;

public interface DataStoreExternal {

    // getter/setter int

    public DataStoreExternal putInteger(String name, int value);

    public int getInteger(String name);

    // getter/setter String

    public DataStoreExternal putString(String name, String value);

    public String getString(String name);

    // getter/setter long

    public DataStoreExternal putLong(String name, long value);

    public long getLong(String name);

    // getter/setter boolean

    public DataStoreExternal putBoolean(String name, boolean data);

    public boolean getBoolean(String name);

    // getter/setter MapHolder

    public DataStoreExternal putMapHolder(MapHolder mapHolder);

    public MapHolder getMapHolder() throws RecoveryException;
}
