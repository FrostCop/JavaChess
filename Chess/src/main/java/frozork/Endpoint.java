package frozork;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/server")
public class Endpoint {
	private static final String OP_ROOMS_INFO = "ROOMS_INFO";
	
	private static final String OP_CREATE_ROOM = "CREATE";
	private static final String OP_CREATE_ROOM_SUCESS = "CREATE_SUCC";
	private static final String OP_CREATE_ROOM_FAILURE = "CREATE_FAIL";

	private static final String OP_JOIN_ROOM = "JOIN";
	private static final String OP_JOIN_ROOM_SUCCESS = "JOIN_SUCC";
	private static final String OP_JOIN_ROOM_FAILURE = "JOIN_FAIL";

	private static final int MAX_ROOMS = 10;

    private static Set<Endpoint> connections = new CopyOnWriteArraySet<>();
    // The room name serves as an id of the room
	// No need of ConcurrentHashMap	because I protect it customly with synchronized(rooms)
    private static Map<String, Room> rooms = new ConcurrentHashMap<String, Room>();	
	
	private Session session;
	
	@OnOpen
	public void start(Session session) {
		System.out.println("Started session");
		
		this.session = session;
		connections.add(this);
		
		// A new client connected. We send the rooms info
		send(this, getRoomsInfoMessage());
	}

	@OnClose
	public void end() {
		System.out.println("Ended session");
		
		connections.remove(this);
		
		// If the client was in a room we need to make him leave the room
		boolean leftARoom = false;
		synchronized(rooms) {
			for(String roomName : rooms.keySet()) {	// We try every room since we don't know where the client might be
				Room.LeaveResult leaveResult = rooms.get(roomName).attemptLeave(this);
				if(leaveResult.success()) {
					leftARoom = true;
					break;
				}
			}
		}
		// If the client left the room we broadcast the new roomInfo
        broadcast(getRoomsInfoMessage());		
	}

	@OnMessage
	public void receive(String message) {
		System.out.println("Received message: " + message);
		
		String[] tokens = message.split("[|]");

		String op = tokens[0];
		switch(op) 
		{
		case OP_CREATE_ROOM:
			handleCreateRoom(tokens[1]);
			break;
		case OP_JOIN_ROOM:
			handleJoinRoom(tokens[1]);
			break;
		}
	}

	private void handleCreateRoom(String roomName) {
		String response;
		String broadcastMessage = null; // populated optionally in the syncrhonized(rooms) but called at the end (broadcasting in syncrhonized is unecessary performance hit)
		
		if(roomName.isBlank()) {
			// Room name is not valid
			response = OP_CREATE_ROOM_FAILURE + "|" + roomName + "|" + "Invalid room name";
		}
		else {
			synchronized (rooms) {
				if(rooms.containsKey(roomName)) {
					// A room with that name already exists
					response = OP_CREATE_ROOM_FAILURE + "|" + roomName + "|" + "Room already exists";
				}
				else if(rooms.size() >= MAX_ROOMS) {
					// Rooms are at maximum number
					response = OP_CREATE_ROOM_FAILURE + "|" + roomName + "|" + "Max rooms reached";
				}
				else {
					// We create the room
					response = OP_CREATE_ROOM_SUCESS + "|" + roomName;
					rooms.put(roomName, new Room(roomName));

					broadcastMessage = getRoomsInfoMessage();	// We will broadcast the new room info
				}
			}
		}

		send(this, response);	// I send the response to the client that just received the message
		
		// If the broadcast message was populated (new room created) we broadcast
		if (broadcastMessage != null) {
            broadcast(broadcastMessage);
        }
	}
	
	private void handleJoinRoom(String roomName) {		
		String response;
		String broadcastMessage = null;

		if(roomName.isBlank()) {
			// Room name is not valid
			response = OP_JOIN_ROOM_FAILURE + "|" + roomName + "|" + "Invalid room name";
		}
		else {            
			// Minimal lock scope: We only lock to FIND the room.
			Room roomToJoin = null;
			synchronized (rooms) {
                roomToJoin = rooms.get(roomName);
            }
			
			if(roomToJoin == null) {
				// A room with that name does not exist
				response = OP_JOIN_ROOM_FAILURE + "|" + roomName + "|" + "Room does not exist";
			}
			else {
				// We attempt to join the room
				Room.JoinResult joinResult = roomToJoin.attemptJoin(this);
				
				if(!joinResult.success()) {
					// We failed to join
					response = OP_JOIN_ROOM_FAILURE + "|" + roomName + "|" + joinResult.failureInfo();
				}
				else {
					// We joined the room
					response = OP_JOIN_ROOM_SUCCESS + "|" + roomName;
					
					broadcastMessage = getRoomsInfoMessage();	// We will broadcast the new room info (connectionsCount) changed for the joined room)
				}
			}
		}

		send(this, response);	// I send the response to the client that just received the message
		
		// If the broadcast message was populated (join success)
		if (broadcastMessage != null) {
            broadcast(broadcastMessage);
        }
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Error");
		t.printStackTrace();
	}

	/**
	 * Composes the rooms info message
	 */
	private String getRoomsInfoMessage() {
		String message = OP_ROOMS_INFO;
		synchronized(rooms) {
			for(String roomName : rooms.keySet()) {
				message += "|" + rooms.get(roomName).getInfo();
			}
		}
		
		return message;
	}
	
	/**
	 * Sends a message to all clients
	 */
	private void broadcast(String message) {
		for(Endpoint connection : connections) {
			send(connection, message);
		}
	}

	/**
	 * Sends a message to a target client
	 * @param target is the server endpoint connected to that client. The connection to that client
	 */
	private void send(Endpoint target, String message) {
		synchronized(target) {
	        try {
                target.getSession().getBasicRemote().sendText(message);
	        } catch (IOException e) {	// If we can't send text to the client we consider it disconnected
	            connections.remove(target);
	            try {
	                target.getSession().close();
	            } catch (IOException e1) { }
	        }
		
		}
	}
	
	public Session getSession() {
		return session;
	}	
}
