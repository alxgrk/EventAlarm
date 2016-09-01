/*
 * Created on 03.11.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.activities.home.maps.EventMap;
import com.alxgrk.eventalarm.info.BandInfo;
import com.alxgrk.eventalarm.info.EventDetails;
import com.alxgrk.eventalarm.info.InfoObject;
import com.alxgrk.eventalarm.requests.MapHolder;

import java.util.List;
import java.util.Set;

public class EventNotificator {

    private Application application;

    private PendingIntent resultPendingIntent;

    private NotificationManager notificationManager;

    public EventNotificator(@NonNull Application application) {
        NullChecker.checkNull(application);

        this.application = application;
        resultPendingIntent = configureIntents();
        notificationManager = (NotificationManager) application.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    private PendingIntent configureIntents() {
        Intent resultIntent = new Intent();
        resultIntent.setClass(application, HomeActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Ensures that navigating back leads to Home Screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }

    /**
     * Creates a notification with the EventDetails of an upcoming event.
     * 
     * @param mapHolder
     */
    public void notificate(@NonNull MapHolder mapHolder) {
        NullChecker.checkNull(mapHolder);

        Set<String> artists = mapHolder.getArtistMap().keySet();
        EventMap eventMap = mapHolder.getEventMap();

        if (!eventMap.getMap().isEmpty()) {
            for (String artist : artists) {
                if (!eventMap.get(artist).isEmpty()) {

                    BandInfo bandInfo = new BandInfo(artist, mapHolder.getImageMap().get(artist));
                    List<InfoObject> events = eventMap.get(artist);

                    notificate(artist, bandInfo, events);
                }
            }
        }
    }

    public void notificate(@NonNull String artist, @NonNull BandInfo bandInfo,
            @NonNull List<InfoObject> events) {
        NullChecker.checkNull(artist);
        NullChecker.checkNull(bandInfo);
        NullChecker.checkNull(events);

        for (InfoObject info : events) {
            EventDetails event = (EventDetails) info;
            notificationManager.notify(artist.hashCode(), createNotification(bandInfo, event));
        }
    }

    private Notification createNotification(BandInfo bandInfo, EventDetails event) {
        String bandName = bandInfo.getBandName();
        String city = event.getCity();
        String date = event.getDate().toString();
        String venue = event.getVenue();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(application) //
                .setSmallIcon(R.drawable.ic_notification_small) //
                .setContentTitle(bandName + " kommt nach " + city + "!") //
                .setContentText("Am " + date + " @ " + venue) //
                .setLargeIcon(bandInfo.getBandImage()) //
                .setAutoCancel(true) //
                .addAction(createSellAction(event.getSwURL()).build()) //
                .setCategory(NotificationCompat.CATEGORY_EVENT) //
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private NotificationCompat.Action.Builder createSellAction(String swURL) {
        int iconNotificationLarge = R.drawable.ic_notification_large;
        CharSequence text = application.getText(R.string.buy);
        PendingIntent activityIntent = PendingIntent.getActivity(application, 0, new Intent(
                Intent.ACTION_VIEW, Uri.parse(swURL)), 0);

        return new NotificationCompat.Action.Builder(iconNotificationLarge, text, activityIntent);
    }
}
