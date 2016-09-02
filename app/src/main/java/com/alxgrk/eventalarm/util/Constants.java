/*
 * Created on 22.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

public class Constants {
    public static final String SPOTIFY_CLIENT_ID = "c73d3e68dbff47fa96d3605d659a5e8b";

    public static final String SPOTIFY_REDIRECT_URI = "eventalarm://callback";

    // for handling the SpotifyLoginActivity result
    public static final int LOGIN_REQUEST = 1;

    // for handling the source of data in HomeActivity
    public static final String FROM_LAUNCHER = "from_launcher";

    // the token to use when making requests on Spotify
    public static String SPOTIFY_REQUEST_TOKEN = "";

    // the id to deliver when asking for events
    public static String APP_ID = "4685735437975648";

    // log tag for LauncherActivity
    public static final String LAUNCHER_TAG = "LauncherActivity";

    // log tag for HomeActivity
    public static final String HOME_TAG = "HomeActivity";

    // log tag for SpotifyArtistsRequest
    public static final String SPOTIFY_ARTIST_REQUEST_TAG = "SpotifyArtistsRequest";

    // log tag for EventRequest
    public static final String EVENT_REQUEST_TAG = "EventRequest";

    // log tag for ImageRequest
    public static final String IMAGE_REQUEST_TAG = "ImageRequest";

    // log tag for StorageHelper
    public static final String STORAGE_HELPER_TAG = "StorageHelper";

    // log tag for HomeStorage
    public static final String HOME_STORAGE_TAG = "HomeStorage";

    // log tag for RequestIntentService
    public static final String BACKGROUND_SERVICE_TAG = "BackgroundService";

    // log tag for BackgroundImageProcessor
    public static final String IMAGE_PROCESSOR_TAG = "ImageProcessor";

    // log tag for ListConfigurator
    public static final String LIST_CONFIGURATOR_TAG = "ListConfigurator";
}