package frozork;

import java.util.ArrayList;
import java.util.List;

/**
 * Every Room is a match with certain time settings mode etc.
 * So it has a Timer, a Board, the Connections... 
 * The match can start only if two people are connected.
 */
public class Room {
	private static final int MAX_CONNECTIONS = 2;
	private static final boolean WHITE = true;
	private static final boolean BLACK = false;
	
	private String name;
	private enum State { WAITING, ONGOING, FINISHED };
	private State state;
	private Timer timer;
	private Board board;
	private Endpoint owner;
	private boolean ownerColor;
	private Endpoint adversary;
	
	// TODO: listen for owner and adversary disconnect and update the state accordingly
	
	public Room(String name, Endpoint owner) {
		this.name = name;
		this.state = State.WAITING;
		this.timer = new Timer();
		this.board = new Board();
		this.owner = owner;
		this.ownerColor = BLACK;	// TODO: get as arg
		this.adversary = null;
	}

    public record JoinResult(boolean success, String failureInfo) { 
    }
    public synchronized JoinResult attemptJoin(Endpoint connection) {
        if(owner.equals(connection) || (adversary != null && adversary.equals(connection))) {
	        	// Connection already in the Room, reject the join
	        	return new JoinResult(false, "Connection already in Room");
        }
        
    		if (adversary != null) {
	        	// Room is full, reject the join
	        	return new JoinResult(false, "Room is full");
        }

        adversary = connection;
        return new JoinResult(true, null);
    }
    
    public record LeaveResult(boolean success, String failureInfo) { 
    }
    public synchronized LeaveResult attemptLeave(Endpoint connection) {
	    	if(!owner.equals(connection) && (adversary == null || !adversary.equals(connection))) {
	        	// Connection not in the Room
	        	return new LeaveResult(false, "Connection not in Room");
        }

	    	if(connection.equals(this.owner)) {
	    		this.owner = null;
	    		if(this.adversary != null) migrateOwner();
	    	}
	    	else {
	    		this.adversary = null;
	    	}
	    	return new LeaveResult(true, null);
    }
    
    public synchronized boolean containsConnection(Endpoint connection) {
    		return (owner != null && owner.equals(connection)) || 
    				(adversary != null && adversary.equals(connection));
    }

	public synchronized String getInfo() {	// Synchronized perché connections.size ha undefined behaviour se l'oggetto sta avendo il suo stato modificato
		String freeColorInfo = (ownerColor == WHITE ? "White" : "Black");
		String timerInfo = timer.getInfo();
		return name + "|" + freeColorInfo + "|" + timerInfo;
	}
	
    // Helpers
	
	private synchronized void migrateOwner() {
		this.owner = this.adversary;
		this.adversary = null;
	}
	
	// Util
	
    public static boolean isValidRoomName(String roomName) {
        return roomName != null && !roomName.isBlank();
    }
}
