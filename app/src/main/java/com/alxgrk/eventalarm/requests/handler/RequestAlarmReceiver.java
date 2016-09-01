/*
 * Created on 22.01.2016
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RequestAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent();
        service.setClass(context, RequestIntentService.class);
        context.startService(service);
    }

}
