package frozork;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

	private static final String OP_LEAVE_ROOM = "LEAVE";
	private static final String OP_LEAVE_ROOM_SUCCESS = "LEAVE_SUCC";
	private static final String OP_LEAVE_ROOM_FAILURE = "LEAVE_FAIL";
	
	private static final int MAX_ROOMS = 10;

    private static Set<Endpoint> connections = new CopyOnWriteArraySet<>();
    // The room name serves as an id of the room
	// No need of ConcurrentHashMap	because I protect it customly with synchronized(rooms)
    private static Map<String, Room> rooms = new HashMap<String, Room>();	
	
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
        if(leftARoom)
        	broadcast(getRoomsInfoMessage());		
	}

	@OnMessage
	public void receive(String message) {
		System.out.println("Received Message: " + message);
		
		String[] tokens = message.split("[|]");

		if(tokens.length < 1) return;
		String op = tokens[0];
		
		// Room Op
		if(tokens.length < 2) return;
		RoomOpOutcome roomOpOutcome = null;
		switch(op) 
		{
		case OP_CREATE_ROOM:
			roomOpOutcome = handleCreateRoom(tokens[1]);
			break;
		case OP_JOIN_ROOM:
			roomOpOutcome = handleJoinLeaveRoom(tokens[1], true);
			break;
		case OP_LEAVE_ROOM:
			roomOpOutcome = handleJoinLeaveRoom(tokens[1], false);
			break;
		}
		if(roomOpOutcome != null)
			roomOpRespond(roomOpOutcome);
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Error");
		t.printStackTrace();
	}
	
	// Rooms Operations
	
	private RoomOpOutcome handleCreateRoom(String roomName) {
        if (!Room.isValidRoomName(roomName)) {
            return roomOpFail(OP_CREATE_ROOM_FAILURE, roomName, "Invalid room name");
        }

        synchronized (rooms) {
            if (rooms.containsKey(roomName)) {
                return roomOpFail(OP_CREATE_ROOM_FAILURE, roomName, "Room already exists");
            }
            if (rooms.size() >= MAX_ROOMS) {
                return roomOpFail(OP_CREATE_ROOM_FAILURE, roomName, "Max rooms reached");
            }

            rooms.put(roomName, new Room(roomName));
            return roomOpOk(OP_CREATE_ROOM_SUCESS, roomName, true);
        }
    }
    private RoomOpOutcome handleJoinLeaveRoom(String roomName, boolean join) {
        final String opFail = join ? OP_JOIN_ROOM_FAILURE : OP_LEAVE_ROOM_FAILURE;
        final String opSucc = join ? OP_JOIN_ROOM_SUCCESS : OP_LEAVE_ROOM_SUCCESS;

        if (!Room.isValidRoomName(roomName)) {
            return roomOpFail(opFail, roomName, "Invalid room name");
        }

        // Minimal lock scope: only lock to FIND the room.
        final Room room;
        synchronized (rooms) {
            room = rooms.get(roomName);
        }

        if (room == null) {
            return roomOpFail(opFail, roomName, "Room does not exist");
        }

        if (join) {
            Room.JoinResult joinResult = room.attemptJoin(this);
            if (!joinResult.success()) {
                return roomOpFail(opFail, roomName, joinResult.failureInfo());
            }
            return roomOpOk(opSucc, roomName, true);
        } else {
            Room.LeaveResult leaveResult = room.attemptLeave(this);
            if (!leaveResult.success()) {
                return roomOpFail(opFail, roomName, leaveResult.failureInfo());
            }
            return roomOpOk(opSucc, roomName, true);
        }
    }
    
    private record RoomOpOutcome(String response, boolean broadcastRoomsInfo) { }
    
    private RoomOpOutcome roomOpOk(String opSuccess, String roomName, boolean broadcastRoomsInfo) {
        return new RoomOpOutcome(opSuccess + "|" + roomName, broadcastRoomsInfo);
    }
    
    private RoomOpOutcome roomOpFail(String opFailure, String roomName, String reason) {
        return new RoomOpOutcome(opFailure + "|" + roomName + "|" + reason, false);
    }
    
    private void roomOpRespond(RoomOpOutcome outcome) {
        send(this, outcome.response());
        if (outcome.broadcastRoomsInfo()) {
            broadcast(getRoomsInfoMessage());
        }
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
	
	// Helpers
	
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
		
		System.out.println("Sending Message: " + message);
	}
	
	public Session getSession() {
		return session;
	}	
}
