/*
 * Created on 27.12.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests.handler;

import com.android.volley.Request;

import android.content.Context;
import android.support.annotation.NonNull;

public interface RequestHandler {

    Context getContext();

    <T> void addToQueue(@NonNull Request<T> request);

    void setWaitingMessage(int message);
}
