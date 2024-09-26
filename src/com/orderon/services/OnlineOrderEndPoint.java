package com.orderon.services;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/OnlineOrder/{outletId}")
public class OnlineOrderEndPoint {

	private Session session;
	private static Set<OnlineOrderEndPoint> endPoint = new CopyOnWriteArraySet<>();
	private HashMap<String, String> outlet = new HashMap<>();
	
	public OnlineOrderEndPoint() {
		// TODO Auto-generated constructor stub
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("outletId") String outletId) {
		 // Get session and WebSocket connection
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		// Handle new messages
	}
	
	@OnClose
	public void onClose(Session session) {
		 // WebSocket connection closes
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		 // Do error handling here
	}
	
}
