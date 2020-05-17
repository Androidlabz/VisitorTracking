package com.androidlabz.visitorlib;

import android.util.Log;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.Socket;

public class VisitorTrackingSocket {
  private final String mAppId;
  private final String mSocketUrl = "http://13.235.167.76:4005/socket/websocket";
  private final String mChannelName = "visitor:100";
  private Socket socket;
  private Channel channel;
  private TrackEventsCallback mTrackEventsCallback;

  public VisitorTrackingSocket(
      String appId) {
    mAppId = appId;
    initSocketConnection();
  }

  private void initSocketConnection() {
    try {
      socket =
          new Socket(mSocketUrl);
      socket.connect();
      initJoinChannel();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initJoinChannel() {
    channel = socket.chan(mChannelName, null);
    try {
      channel.join().receive("ok", envelope -> {
        Log.e("connected", envelope.toString());
      }).receive("ignore", envelope -> {
        Log.e("ignored", envelope.toString());
      });
      channel.on("recieve_comment", envelope -> {
        Log.e("recieved envelope", envelope.toString());
        mTrackEventsCallback.onReceiveComment(envelope);
      });
      channel.on("receive_user_online_count", envelope -> {
        Log.e("recieved envelope", envelope.toString());
        mTrackEventsCallback.onRecieveUserOnlineCount(envelope);
      });
      channel.on("receive_user_offline_count", envelope -> {
        Log.e("recieved envelope", envelope.toString());
        mTrackEventsCallback.onRecieveUserOfflineCount(envelope);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void pushComment(String userId, String userName, String comment) {
    if (socket == null || !socket.isConnected()) {
      initSocketConnection();
    }
    if (socket != null && socket.isConnected()) {
      ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
          .put("id", userId)
          .put("name", userName)
          .put("comment",
              comment);
      if (channel != null) {
        try {
          Log.e("push", node.toString());
          channel.push("push_comment", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push("push_comment", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void pushUserOnline() {
    if (socket == null || !socket.isConnected()) {
      initSocketConnection();
    }
    if (socket != null && socket.isConnected()) {
      ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
      if (channel != null) {
        try {
          Log.e("push", node.toString());
          channel.push("push_user_online", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push("push_user_online", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void pushUserOffline() {
    if (socket == null || !socket.isConnected()) {
      initSocketConnection();
    }
    if (socket != null && socket.isConnected()) {
      ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
      if (channel != null) {
        try {
          Log.e("push", node.toString());
          channel.push("push_user_offline", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push("push_user_offline", node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void disconnectAllSockets() {
    if (socket != null) {
      try {
        socket.removeAllChannels();
        socket.disconnect();
        socket = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void setTrackEventsCallback(
      TrackEventsCallback trackEventsCallback) {
    mTrackEventsCallback = trackEventsCallback;
  }

  public interface TrackEventsCallback {
    void onReceiveComment(Envelope envelope);

    void onRecieveUserOnlineCount(Envelope envelope);

    void onRecieveUserOfflineCount(Envelope envelope);
  }
}
