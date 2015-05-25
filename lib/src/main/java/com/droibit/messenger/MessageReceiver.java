package com.droibit.messenger;

import android.support.annotation.Nullable;

/**
 * It is called to receive the message of the registered path.
 *
 * @author kumagai
 * @since 2015/05/22
 */
public interface MessageReceiver {

    /**
     * Called in the message receive.
     *
     * @param messenger messenger object
     * @param data data to be associated with the path
     */
    void onMessageReceived(Messenger messenger, @Nullable String data);
}
