package com.droibit.messenger;

import android.support.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @auther kumagai
 * @since 15/05/23
 */
public class MessengerTest {

    private static final String PATH_TEST = "/test";
    private static final String PATH_MESSENGER = "/messenger";

    private Messenger mMessenger;

    @Before
    public void setUp() throws Exception {
        mMessenger = new Messenger(null);
    }

    @Test
    public void testRegisterWithClear() throws Exception {

        mMessenger.registerReceiver(PATH_TEST, new MessageReceiver() {
            @Override
            public void onMessageReceived(Messenger messenger, @Nullable String data) {
            }
        });
        assertThat(mMessenger.getReceivers().keySet(), hasItem(PATH_TEST));
        assertThat(mMessenger.getReceivers().size(), is(1));
        assertNotNull(mMessenger.getReceivers().get(PATH_TEST));

        mMessenger.registerReceiver(PATH_MESSENGER, new MessageReceiver() {
            @Override
            public void onMessageReceived(Messenger messenger, @Nullable String data) {
            }
        });
        assertThat(mMessenger.getReceivers().keySet(), hasItem(PATH_MESSENGER));
        assertThat(mMessenger.getReceivers().size(), is(2));
        assertNotNull(mMessenger.getReceivers().get(PATH_MESSENGER));

        mMessenger.clearReceivers();
        assertThat(mMessenger.getReceivers().isEmpty(), is(true));
        assertThat(mMessenger.getReceivers().containsKey(PATH_MESSENGER), is(false));
        assertThat(mMessenger.getReceivers().containsKey(PATH_TEST), is(false));
    }

    @Test
    public void testUnregister() throws Exception {

        mMessenger.registerReceiver(PATH_TEST, new MessageReceiver() {
            @Override
            public void onMessageReceived(Messenger messenger, @Nullable String data) {
            }
        });
        mMessenger.registerReceiver(PATH_MESSENGER, new MessageReceiver() {
            @Override
            public void onMessageReceived(Messenger messenger, @Nullable String data) {
            }
        });

        mMessenger.unregisterReceiver(PATH_TEST);
        assertThat(mMessenger.getReceivers().containsKey(PATH_TEST), is(false));

        mMessenger.unregisterReceiver(PATH_MESSENGER);
        assertThat(mMessenger.getReceivers().containsKey(PATH_TEST), is(false));

        assertThat(mMessenger.getReceivers().isEmpty(), is(true));
    }

    @Test
    public void testRejectDecider() throws Exception {
        assertNull(mMessenger.getRejectDecider());

        mMessenger.setRejectDecider(new Messenger.RejectDecider() {
            @Override
            public boolean shouldReject() {
                return false;
            }
        });
        assertNotNull(mMessenger.getRejectDecider());

        mMessenger.setRejectDecider(null);
        assertNull(mMessenger.getRejectDecider());
    }
}