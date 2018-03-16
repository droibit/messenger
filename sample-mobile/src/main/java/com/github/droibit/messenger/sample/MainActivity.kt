package com.github.droibit.messenger.sample

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.github.droibit.messenger.Messenger
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.wearable.CapabilityClient
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

class MainActivity : Activity(), ConnectionCallbacks {

  private lateinit var messenger: Messenger

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    messenger = Messenger.Builder(this)
        .getNodesTimeout(2_000L)
        .excludeNode { !it.isNearby }
        .build()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    val id = item.itemId
    return if (id == R.id.action_settings) true else super.onOptionsItemSelected(item)
  }

  override fun onConnected(bundle: Bundle?) {
    Timber.d("#onConnected")
  }

  override fun onConnectionSuspended(i: Int) {}

  fun onSendMessage(v: View) {
    sendMessage(PATH_DEFAULT_MESSAGE, "Hello, world")
  }

  fun onSendErrorMessage(v: View) {
    sendMessage(PATH_ERROR_MESSAGE, "Not connected to the network")
  }

  fun onSendErrorMessage2(v: View) {
    sendMessage(PATH_ERROR_MESSAGE, "Oops")
  }

  fun onSendSuccessMessage(v: View) {
    sendMessage(PATH_SUCCESS_MESSAGE, "Authenticated")
  }

  fun onSendMessageWithReceiveMessage(v: View) {
    launch(UI) {
      try {
        val event = messenger.obtainMessage(
            PATH_REQUEST_MESSAGE, null,
            setOf(PATH_REQUEST_MESSAGE_FROM_WEAR)
        )
        val data = event.data.toString(Charsets.UTF_8)
        Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT)
            .show()
      } catch (e: Exception) {
        Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  fun onSendMessageWithCapability(v: View) {
    launch(UI) {
      Timber.d("#onSendMessageWithCapability()")
      val capabilityInfo = messenger.getCapability(
          "verify_remote_sample_wear_app",
          CapabilityClient.FILTER_REACHABLE
      )
      Timber.d(
          "name: ${capabilityInfo.name}, nodes: ${capabilityInfo.nodes.map { "${it.displayName}, nearBy=${it.isNearby}" }}"
      )

      val node = capabilityInfo.nodes.firstOrNull { it.isNearby }
      if (node == null) {
        Toast.makeText(this@MainActivity, "Not found node.", Toast.LENGTH_SHORT)
            .show()
        return@launch
      }

      try {
        messenger.sendMessage(
            node.id, PATH_DEFAULT_MESSAGE,
            "Hello, World with Capability".toByteArray()
        )
        Toast.makeText(this@MainActivity, "Successful send of message.", Toast.LENGTH_SHORT)
            .show()
      } catch (e: Exception) {
        Timber.w(e)
        Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  private fun sendMessage(
    path: String,
    message: String?
  ): Job {
    return launch(UI) {
      Timber.d("#sendMessage($message, to=$path) in ${Thread.currentThread().name}.")
      try {
        messenger.sendMessage(path, message?.toByteArray())

        Toast.makeText(this@MainActivity, "Successful send of message.", Toast.LENGTH_SHORT)
            .show()
      } catch (e: Exception) {
        Timber.w(e)
        Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }

  companion object {

    private const val PATH_DEFAULT_MESSAGE = "/message"
    private const val PATH_ERROR_MESSAGE = "/error_message"
    private const val PATH_SUCCESS_MESSAGE = "/success_message"
    private const val PATH_REQUEST_MESSAGE = "/request_message"
    private const val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
  }
}
