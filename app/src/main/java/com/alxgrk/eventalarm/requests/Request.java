/*
 * Created on 27.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests;

import com.alxgrk.eventalarm.activities.home.maps.RequestMap;

public interface Request {
    void makeRequest();

    @SuppressWarnings("rawtypes")
    RequestMap getResult();
}
