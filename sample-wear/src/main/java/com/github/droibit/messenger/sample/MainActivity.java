package com.github.droibit.messenger.sample;

import android.app.Activity;
import android.os.Bundle;

import com.github.droibit.messenger.Messenger;
import com.github.droibit.messenger.sample.model.ConfirmMessageReceiver;
import com.github.droibit.messenger.sample.model.ResponseMessageReceiver;
import com.github.droibit.messenger.sample.model.StandardMessageReceiver;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import static com.github.droibit.messenger.sample.model.ConfirmMessageReceiver.*;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

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

        messenger = new Messenger.Builder(googleApiClient)
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
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(googleApiClient, messenger);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: Implement
    }
}
