# VisitorTracking
Android app to track visitors count and show live comments

Add the JitPack repository to your build file

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        implementation 'com.github.androidlabz:VisitorTracking:1.0.5'
	}
	
	 Initialize Socket :
	
	 private VisitorTrackingSocket mTrackingSocket;

         @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
           mTrackingSocket=new VisitorTrackingSocket("VisitorTrack");
           mTrackingSocket.setTrackEventsCallback(this);
         }
	 
	 Register callback in your activity or fragment to implement callback methods
	 
	  mTrackingSocket.setTrackEventsCallback(this);
	  
	  @Override public void onReceiveComment(Envelope envelope) {
      //Comment will be recieved in this event payload
      //String  userId = envelope.getPayload().get("id").asText();
     //String userName = envelope.getPayload().get("name").asText();
     //String recieveComment = envelope.getPayload().get("comment").asText();
      //Use the above values in your recylerview
     }

    @Override public void onRecieveUserOnlineCount(Envelope envelope) {
      int visitorCount = envelope.getPayload().get("visitor_count").asInt();
     }

    @Override public void onRecieveUserOfflineCount(Envelope envelope) {
      int visitorCount = envelope.getPayload().get("visitor_count").asInt();
    }

    @Override public void onJoinChannelSuccess(Envelope envelope) {
    //Channel is joined successfully
    }

    @Override public void onJoinChannelIgnored(Envelope envelope) {
     //Channel is ignored 
   }
	  
	  
	     
	     
