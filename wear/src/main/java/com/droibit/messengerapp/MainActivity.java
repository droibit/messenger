package com.droibit.messengerapp;

import android.app.Activity;
import android.os.Bundle;

import com.droibit.messenger.Messenger;
import com.droibit.messengerapp.model.ConfirmMessageReceiver;
import com.droibit.messengerapp.model.ResponseMessageReceiver;
import com.droibit.messengerapp.model.StandardMessageReceiver;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import static android.support.wearable.activity.ConfirmationActivity.FAILURE_ANIMATION;
import static android.support.wearable.activity.ConfirmationActivity.SUCCESS_ANIMATION;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

    public static final String PATH_DEFAULT_MESSAGE = "/message";
    public static final String PATH_ERROR_MESSAGE = "/error_message";
    public static final String PATH_SUCCESS_MESSAGE = "/success_message";
    public static final String PATH_REQUEST_MESSAGE = "/request_message";
    public static final String PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear";

    private GoogleApiClient mGoogleApiClient;
    private Messenger mMessenger;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Wearable.API)
                            .addConnectionCallbacks(this)
                            .build();

        mMessenger = new Messenger(mGoogleApiClient);
        mMessenger.setRejectDecider(new Messenger.RejectDecider() {
            @Override public boolean shouldReject() {
                return false;
            }
        });
        mMessenger.registerReceiver(PATH_DEFAULT_MESSAGE, new StandardMessageReceiver(this));
        mMessenger.registerReceiver(PATH_ERROR_MESSAGE, new ConfirmMessageReceiver(this, FAILURE_ANIMATION));
        mMessenger.registerReceiver(PATH_SUCCESS_MESSAGE, new ConfirmMessageReceiver(this, SUCCESS_ANIMATION));
        mMessenger.registerReceiver(PATH_REQUEST_MESSAGE, new ResponseMessageReceiver());
    }

    /** {@inheritDoc} */
    @Override
    protected void onResume() {
        super.onResume();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Wearable.MessageApi.removeListener(mGoogleApiClient, mMessenger);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, mMessenger);
    }

    /** {@inheritDoc} */
    @Override
    public void onConnectionSuspended(int i) {
        // TODO: Implement
    }
}
