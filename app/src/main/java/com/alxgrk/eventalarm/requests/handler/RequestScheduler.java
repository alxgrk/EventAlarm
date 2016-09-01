/*
 * Created on 22.01.2016
 *
 * author Alex
 */
package com.alxgrk.eventalarm.requests.handler;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alxgrk.eventalarm.util.NullChecker;

import java.util.List;

import static com.alxgrk.eventalarm.util.Constants.BACKGROUND_SERVICE_TAG;

public class RequestScheduler {

    public static final int REQUEST_CODE = 84685;

    private Context appContext;

    public RequestScheduler(@NonNull Context appContext) {
        NullChecker.checkNull(appContext);

        this.appContext = appContext;
    }

    public void scheduleRequestService() {
        if (!isAlarmReceiverRunning()) {
            getAlarmManager().setInexactRepeating(AlarmManager.RTC_WAKEUP, System
                    .currentTimeMillis() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR,
                    getRequestIntent());
        }

        Log.d(BACKGROUND_SERVICE_TAG, "background service scheduled");
    }

    private boolean isAlarmReceiverRunning() {
        ComponentName name = new ComponentName(appContext, RequestAlarmReceiver.class);
        ActivityManager manager = (ActivityManager) appContext.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);

        boolean running = false;
        for (RunningServiceInfo runningService : runningServices) {
            if (!running)
                running = runningService.service.equals(name);
            else
                break;
        }
        return running;
    }

    public void cancelRequestService() {
        getAlarmManager().cancel(getRequestIntent());

        Log.d(BACKGROUND_SERVICE_TAG, "background service canceled");
    }

    private AlarmManager getAlarmManager() {
        AlarmManager alarm = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        return alarm;
    }

    private PendingIntent getRequestIntent() {
        Intent intent = new Intent();
        intent.setClass(appContext, RequestAlarmReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(appContext, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pIntent;
    }
}
