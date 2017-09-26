package com.github.droibit.messenger.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.droibit.messenger.MessageCallback;
import com.github.droibit.messenger.Messenger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements ConnectionCallbacks {

    private static final String PATH_DEFAULT_MESSAGE = "/message";
    private static final String PATH_ERROR_MESSAGE = "/error_message";
    private static final String PATH_SUCCESS_MESSAGE = "/success_message";
    private static final String PATH_REQUEST_MESSAGE = "/request_message";

    private GoogleApiClient googleApiClient;
    private Messenger messenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                                    .addApi(Wearable.API)
                                    .addConnectionCallbacks(this)
                                    .build();

        messenger = new Messenger(googleApiClient);
        messenger.registerReceiver(new WearMessageReceiver(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            Wearable.MessageApi.removeListener(googleApiClient, messenger);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(googleApiClient, messenger);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public void onSendMessage(View v) {
        messenger.sendMessage(PATH_DEFAULT_MESSAGE, "Hello, world", new MessageCallback() {
            @Override
            public void onMessageResult(@NonNull Status status) {
                if (!status.isSuccess()) {
                    Log.d(BuildConfig.BUILD_TYPE, "Failed send message : " + status.getStatusCode());
                }
            }
        });
    }

    public void onSendErrorMessage(View v) {
        messenger.sendMessage(PATH_ERROR_MESSAGE, "Not connected to the network", null);
    }

    public void onSendErrorMessage2(View v) {
        messenger.sendMessage(PATH_ERROR_MESSAGE, "Oops!!", null);
    }

    public void onSendSuccessMessage(View v) {
        messenger.sendMessage(PATH_SUCCESS_MESSAGE, "Authenticated", null);
    }

    public void onSendMessageWithReceiveMessage(View v) {
        messenger.sendMessage(PATH_REQUEST_MESSAGE, null, null);
    }

    public void onReceiveMessageWithReject(View v) {
    }
}
