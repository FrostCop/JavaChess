// The usual flow of calling is endpoint -> state -> view.
// So the most high level is endpoint. Endpoint calls view directly only for minor things that don't change local state like displaying a feedback message.
// When the client interacts with the page the flow is simply view -> endpoint (sending message)

// Constants
const WEBSOCKET_HOST = "ws://127.0.0.1:8080/Chess/websocket/server"
//const WEBSOCKET_HOST = "ws://147.53.255.127:8080/Chess/websocket/server"

const OP_ROOMS_INFO = "ROOMS_INFO";

const OP_CREATE_ROOM = "CREATE";
const OP_CREATE_ROOM_SUCESS = "CREATE_SUCC";
const OP_CREATE_ROOM_FAILURE = "CREATE_FAIL";

const OP_JOIN_ROOM = "JOIN";
const OP_JOIN_ROOM_SUCCESS = "JOIN_SUCC";
const OP_JOIN_ROOM_FAILURE = "JOIN_FAIL";

const OP_LEAVE_ROOM = "LEAVE";
const OP_LEAVE_ROOM_SUCCESS = "LEAVE_SUCC";
const OP_LEAVE_ROOM_FAILURE = "LEAVE_FAIL";

// View
var view = {
	init: function(){		
		// Create Event
		document.getElementById("create-submit").onclick = function() {
			var createNameElement = document.getElementById("create-name");
			if(createNameElement.value.length > 0) endpoint.sendCreateMessage(createNameElement.value);			
			createNameElement.value = "";
		}
	},
	
	clearRoomList: function(){
		let roomList = document.getElementById("room-list");
		roomList.textContent = "";	// We remove all the childs
	},
	
	makeRoomListEntry: function(name, connectionsCount) {
		let roomListEntry = document.createElement("div");
		roomListEntry.className = "room-list-entry";
		
		let roomName = document.createElement("p");
		roomName.className = "room-list-entry-name";
		roomName.innerHTML = name;
		roomListEntry.appendChild(roomName);

		let roomOccupationContainer = document.createElement("div");
		roomOccupationContainer.className = "room-list-entry-occupation-container";
		let roomOccupation = document.createElement("p");
		roomOccupation.className = "room-list-entry-occupation";
		roomOccupation.innerHTML = connectionsCount + "/2";
		roomOccupationContainer.appendChild(roomOccupation);
		roomListEntry.appendChild(roomOccupationContainer);
		let roomJoin = document.createElement("button");
		roomJoin.className = "room-list-entry-join";
		roomJoin.onclick = () => endpoint.sendJoinMessage(name);
		let roomJoinLabel = document.createElement("p");
		roomJoinLabel.className = "room-list-entry-join-label";
		roomJoinLabel.innerHTML = "Join";
		roomJoin.appendChild(roomJoinLabel);
		roomListEntry.appendChild(roomJoin);

		
		document.getElementById("room-list").appendChild(roomListEntry);
	},

	setFeedback: function(feedback) {
		let feedbackElement = document.getElementById("feedback");
		if(feedback == "")
			feedbackElement.style.display = "none";
		else {
			feedbackElement.style.display = "block";
			feedbackElement.innerHTML = feedback;
		}
	},

	switch: function(toRoomsView) {
		let roomsView = document.getElementById("view-rooms");
		let gameView = document.getElementById("view-game");
		
		roomsView.style.display = (toRoomsView ? "flex" : "none");
		gameView.style.display = (toRoomsView ? "none" : "flex");
	},

	setJoinedRoomInfo: function(room) {
		document.getElementById("room-name").innerHTML = "Room - " + room.name;
	}
}

// State
class Room {
	constructor(name, occupations) {
		this.name = name;
		this.occupations = occupations;
	}
}

var state = {
	rooms: [],
	joinedRoomName: null,
	
	init: function() {
		this.rooms = [];
		this.joinedRoomName = null;
	},
	
	clearRooms: function() {
		this.rooms = [];
		view.clearRoomList();
	},	
	addRoom: function(room) {
		this.rooms.push(room);
		view.makeRoomListEntry(room.name, room.occupations);
	},
	setRooms: function(rooms) {
		this.clearRooms();
		rooms.forEach(room => this.addRoom(new Room(room.name, room.occupations)));
	},
	
	joinRoom: function(roomName) {
		let foundRoom = this.rooms.find(room => room.name == roomName);
		if(foundRoom == undefined) {
			console.log("Can't join room because the room could not be found!");
			return;
		}
		
		this.joinedRoomName = roomName;
		view.setJoinedRoomInfo(foundRoom);
		view.switch(false);
	}
}

// Endpoint
var endpoint = {
    socket: null,	
	socketActive: false,
	
    connect: function(host) {
        this.socket = new WebSocket(host);
		
        this.socket.onopen = function() {
			console.log("Socket Opened");
			endpoint.socketActive = true;
        }
		
        this.socket.onclose = function() {
			console.log("Socket Closed");
			endpoint.socketActive = false;
        }
		
        this.socket.onmessage = function(message) {
			console.log("Socket Received Message " + message.data);
			
			const tokens = message.data.split("|");
			
			let op = tokens[0];
			
			// Room Op
			switch(op) 
			{
			case OP_ROOMS_INFO:
				let rooms = [];
				for(let t = 1; t + 1 < tokens.length; t += 2) // Foreach token after the first
					rooms.push(new Room(tokens[t], tokens[t + 1])); // We get the room name and the room connections count				
				endpoint.handleRoomsInfo(rooms);
				break;				
			case OP_CREATE_ROOM_FAILURE:
				endpoint.handleCreateRoomFailure(tokens[1], tokens[2]);
				break;				
			case OP_CREATE_ROOM_SUCESS:
				endpoint.handleCreateRoomSuccess(tokens[1]);
				break;				
			case OP_JOIN_ROOM_FAILURE:
				endpoint.handleJoinRoomFailure(tokens[1], tokens[2]);
				break;				
			case OP_JOIN_ROOM_SUCCESS:
				endpoint.handleJoinRoomSuccess(tokens[1]);
				break;
			}
        }
    },

	handleRoomsInfo: function(rooms) {
		state.setRooms(rooms);
	},
	handleCreateRoomFailure: function(roomName, reason) {
		view.setFeedback("Creation failure of room \"" + roomName  + "\" because<br>" + reason);	
	},
	handleCreateRoomSuccess: function(roomName) {
		view.setFeedback("Creation success of room \"" + roomName + "\"");
	},
	handleJoinRoomFailure: function(roomName, reason) {
		view.setFeedback("Join failure of room \"" + roomName  + "\" because<br>" + reason);
	},
	handleJoinRoomSuccess: function(roomName) {
		view.setFeedback("Join success of room \"" + roomName + "\"");
		state.joinRoom(roomName);
	},
	
	send: function(message) {	// returns wether it was succesful
		if(this.socket == null) {
			console.log("Can't send message " + message + " because socket is null");
			return false;
		}
		if(!this.socketActive) {
			console.log("Can't send message " + message + " because socket is not active");
			return false;
		}
		
		console.log("Socket Send Message " + message);		
		endpoint.socket.send(message);
		return true;
	},	
	sendCreateMessage: function(roomName) {
		this.send(OP_CREATE_ROOM + "|" + roomName);
	},	
	sendJoinMessage: function(roomName) {
		this.send(OP_JOIN_ROOM + "|" + roomName);
	}
}

view.init();
state.init();
endpoint.connect(WEBSOCKET_HOST);