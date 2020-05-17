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
  private static final String mSocketUrl = "http://13.235.167.76:4005/socket/websocket";
  private static final String mChannelName = "visitor:100";
  private static final String mPushCommentEvent = "push_comment";
  private static final String mPushUserOnlineEvent = "push_user_online";
  private static final String mPushUserOfflineEvent = "push_user_offline";
  private static final String mReceiveUserOnlineEvent = "receive_user_online_count";
  private static final String mReceiveUserOfflineEvent = "receive_user_offline_count";
  private static final String mReceiveCommentEvent = "receive_comment";

  private Socket socket;
  private Channel channel;
  private TrackEventsCallback mTrackEventsCallback;

  public VisitorTrackingSocket(
      String appId) {
    mAppId = appId;
  }

  public void initSocketConnection() {
    if (socket == null || !socket.isConnected()) {
      try {
        socket =
            new Socket(mSocketUrl);
        socket.connect();
        if (channel == null) {
          initJoinChannel();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void initJoinChannel() {
    channel = socket.chan(mChannelName, null);
    try {
      channel.join().receive("ok", envelope -> {
        Log.e("connected", envelope.toString());
        mTrackEventsCallback.onJoinChannelSuccess(envelope);
      }).receive("ignore", envelope -> {
        Log.e("ignored", envelope.toString());
        mTrackEventsCallback.onJoinChannelIgnored(envelope);
      });
      channel.on(mReceiveCommentEvent, envelope -> {
        mTrackEventsCallback.onReceiveComment(envelope);
      });
      channel.on(mReceiveUserOnlineEvent, envelope -> {
        mTrackEventsCallback.onRecieveUserOnlineCount(envelope);
      });
      channel.on(mReceiveUserOfflineEvent, envelope -> {
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
          channel.push(mPushCommentEvent, node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push(mPushCommentEvent, node);
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
          channel.push(mPushUserOnlineEvent, node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push(mPushUserOnlineEvent, node);
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
          channel.push(mPushUserOfflineEvent, node);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        initJoinChannel();
        try {
          Log.e("push", node.toString());
          channel.push(mPushUserOfflineEvent, node);
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

    void onJoinChannelSuccess(Envelope envelope);

    void onJoinChannelIgnored(Envelope envelope);
  }
}
