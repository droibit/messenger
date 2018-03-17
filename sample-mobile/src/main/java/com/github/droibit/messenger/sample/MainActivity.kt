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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

class MainActivity : Activity() {

  private val messenger: Messenger by lazy {
    Messenger.Builder(this)
        .getNodesTimeout(2_000L)
        .excludeNode { !it.isNearby }
        .build()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

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

  fun onObtainMessage(v: View) {
    launch(UI) {
      val message = try {
        val event = messenger.obtainMessage(
            PATH_REQUEST_MESSAGE, null,
            setOf(PATH_REQUEST_MESSAGE_FROM_WEAR)
        )
        event.data.toString(Charsets.UTF_8)
      } catch (e: Exception) {
        Timber.w(e)
        "Error: ${e.message}"
      }
      Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
          .show()
    }
  }

  fun onSendMessageWithCapability(v: View) = launch(UI) {
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

    val message = try {
      messenger.sendMessage(
          node.id, PATH_DEFAULT_MESSAGE,
          "Hello, World with Capability".toByteArray()
      )
      "Successful send of message."
    } catch (e: Exception) {
      Timber.w(e)
      e.message
    }
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
        .show()
  }

  fun onGetConnectedNodes(v: View) = launch(UI) {
    val message = try {
      val connectedNodes = messenger.getConnectedNodes()
      if (connectedNodes.isEmpty()) {
        "Not connected."
      } else {
        val displayNames = connectedNodes.joinToString(",") { it.displayName }
        "Connected $displayNames."
      }
    } catch (e: Exception) {
      Timber.w(e)
      e.message
    }
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT)
        .show()
  }

  private fun sendMessage(
    path: String,
    message: String?
  ) = launch(UI) {
    Timber.d("#sendMessage($message, to=$path) in ${Thread.currentThread().name}.")
    val resultMessage = try {
      messenger.sendMessage(path, message?.toByteArray())
      "Successful send of message."
    } catch (e: Exception) {
      Timber.w(e)
      e.message
    }
    Toast.makeText(this@MainActivity, resultMessage, Toast.LENGTH_SHORT).show()
  }

  companion object {

    private const val PATH_DEFAULT_MESSAGE = "/message"
    private const val PATH_ERROR_MESSAGE = "/error_message"
    private const val PATH_SUCCESS_MESSAGE = "/success_message"
    private const val PATH_REQUEST_MESSAGE = "/request_message"
    private const val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
  }
}
