package frozork;

import java.util.ArrayList;
import java.util.List;

public class Room {
	private static final int MAX_CONNECTIONS = 2;
	
	private String name;
	private List<Endpoint> connections;
	
	public Room(String name) {
		this.name = name;
		this.connections = new ArrayList<Endpoint>();
	}
	
	public synchronized String getInfo() {	// Synchronized perché connections.size ha undefined behaviour se l'oggetto sta avendo il suo stato modificato
		return name + "|" + connections.size();
	}
	
    public synchronized JoinResult attemptJoin(Endpoint connection) {
        if (connections.size() >= MAX_CONNECTIONS) {
        	// Room is full, reject the join
        	return new JoinResult(false, "Room is full");
        }
        
        if(connections.contains(connection)) {
        	// Connection already in the Room, reject the join
        	return new JoinResult(false, "Connection already in Room");
        }
        
        connections.add(connection);
        return new JoinResult(true, null);
    }
    public record JoinResult(boolean success, String failureInfo) { }
    
    public synchronized LeaveResult attemptLeave(Endpoint connection) {
    	if(!connections.contains(connection)) {
        	// Connection not in the Room
        	return new LeaveResult(false, "Connection not in Room");
        }
    	
    	connections.remove(connection);
    	return new LeaveResult(true, null);
    }
    public record LeaveResult(boolean success, String failureInfo) { }
}
