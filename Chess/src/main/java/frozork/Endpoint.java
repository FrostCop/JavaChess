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

/**
 * Each instance of this class is a client/user/connection
 */
@ServerEndpoint(value = "/websocket/server")
public class Endpoint {
	private static final String OP_USER_INFO = "USER_INFO";
	private static final String OP_USER_INFO_SUCC = "USER_INFO_SUCC";
	private static final String OP_USER_INFO_FAIL = "USER_INFO_FAIL";
	
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
	private String nickname;
	
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
		
		// Op
		if(tokens.length < 2) return;
		OpOutcome opOutcome = null;
		switch(op) 
		{
		case OP_USER_INFO:
			opOutcome = handleUserInfo(tokens[1]);
			break;
		case OP_CREATE_ROOM:
			opOutcome = handleCreateRoom(tokens[1]);
			break;
		case OP_JOIN_ROOM:
			opOutcome = handleJoinLeaveRoom(tokens[1], true);
			break;
		case OP_LEAVE_ROOM:
			opOutcome = handleJoinLeaveRoom(tokens[1], false);
			break;
		}
		if(opOutcome != null)
			opRespond(opOutcome);
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		System.err.println("Error");
		t.printStackTrace();
	}
	
	// Ops
	private OpOutcome handleUserInfo(String nickname) {
        if (!isValidNickname(nickname)) {
        		return new OpOutcome(OP_USER_INFO_FAIL + "|" + nickname + "|" + "Nickname is not valid", false);
        }

        this.nickname = nickname;
		return new OpOutcome(OP_USER_INFO_SUCC + "|" + nickname, false);
    }

	private OpOutcome handleCreateRoom(String roomName) {
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
            return roomOpOk(OP_CREATE_ROOM_SUCESS, roomName);
        }
    }
	
    private OpOutcome handleJoinLeaveRoom(String roomName, boolean join) {
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
            return roomOpOk(opSucc, roomName);
        } else {
            Room.LeaveResult leaveResult = room.attemptLeave(this);
            if (!leaveResult.success()) {
                return roomOpFail(opFail, roomName, leaveResult.failureInfo());
            }
            return roomOpOk(opSucc, roomName);
        }
    }
    
    private record OpOutcome(String response, boolean broadcastRoomsInfo) { }
    
    private OpOutcome roomOpOk(String opSuccess, String roomName) {
        return new OpOutcome(opSuccess + "|" + roomName, true);
    }
    
    private OpOutcome roomOpFail(String opFailure, String roomName, String reason) {
        return new OpOutcome(opFailure + "|" + roomName + "|" + reason, false);
    }
    
    private void opRespond(OpOutcome outcome) {
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
	
	public Session getSession() {
		return session;
	}
	
	// Helpers
	
	/**
	 * Sends a message to all clients
	 */
	private static void broadcast(String message) {
		for(Endpoint connection : connections) {
			send(connection, message);
		}
	}

	/**
	 * Sends a message to a target clients
	 * @param target is the server endpoint connected to that client. The connection to that client
	 */
	private static void send(Endpoint target, String message) {
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
	
	private static boolean isValidNickname(String nickname) {
		return nickname.length() > 0;
	}
}
