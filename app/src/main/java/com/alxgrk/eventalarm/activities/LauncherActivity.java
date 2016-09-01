package com.alxgrk.eventalarm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.activities.home.HomeActivity;
import com.alxgrk.eventalarm.util.ErrorToast;
import com.alxgrk.eventalarm.util.datastores.StorageHelper;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.alxgrk.eventalarm.util.Constants.*;

public class LauncherActivity extends Activity {

    private StorageHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        storageHelper = StorageHelper.getInstance(getApplication());

        final AuthenticationRequest request = new AuthenticationRequest.Builder(SPOTIFY_CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, SPOTIFY_REDIRECT_URI).setScopes(new String[] {
                        "user-follow-read" }).build();

        AuthenticationClient.openLoginActivity(this, LOGIN_REQUEST, request);
        // TODO smooth waiting screen
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                case TOKEN:
                    SPOTIFY_REQUEST_TOKEN = "Bearer " + response.getAccessToken();
                    Log.d(LAUNCHER_TAG, "Token: " + SPOTIFY_REQUEST_TOKEN);
                    storageHelper.putString("SPOTIFY_REQUEST_TOKEN", SPOTIFY_REQUEST_TOKEN);
                    startHomeActivity();
                    break;

                case ERROR:
                    Log.e(LAUNCHER_TAG, "Token could not be received! " + response.getError());
                    ErrorToast.displayError(getApplicationContext(),
                            R.string.error_spotify_registration);
                    break;

                default:
                    Log.d(LAUNCHER_TAG, "something went wrong... " + response.getType());
            }
        }
    }

    private void startHomeActivity() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
