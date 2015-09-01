package com.droibit.messengerapp;

import android.app.Activity;
import android.os.Bundle;

import com.droibit.messenger.Messenger;
import com.droibit.messengerapp.model.ConfirmMessageReceiver;
import com.droibit.messengerapp.model.ResponseMessageReceiver;
import com.droibit.messengerapp.model.StandardMessageReceiver;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import static com.droibit.messengerapp.model.ConfirmMessageReceiver.*;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

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

        mMessenger = new Messenger.Builder(mGoogleApiClient)
                                  .register(new StandardMessageReceiver(this))
                                  .register(new ConfirmMessageReceiver(this, PATH_SUCCESS_MESSAGE))
                                  .register(new ConfirmMessageReceiver(this, PATH_ERROR_MESSAGE))
                                  .register(new ResponseMessageReceiver())
                                  .rejectDecider(new Messenger.RejectDecider() {
                                        @Override
                                        public boolean shouldReject() {
                                            return false;
                                        }
                                    })
                                  .get();
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
