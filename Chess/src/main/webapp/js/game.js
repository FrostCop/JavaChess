// The usual flow of calling is endpoint -> state -> view.
// So the most high level is endpoint. Endpoint calls view directly only for minor things that don't change local state like displaying a feedback message.
// When the client interacts with the page the flow is simply view -> endpoint (sending message)

// Constants
const WEBSOCKET_HOST = "ws://127.0.0.1:8080/Chess/websocket/server"
//const WEBSOCKET_HOST = "ws://147.53.255.127:8080/Chess/websocket/server"

const OP_USER_INFO = "USER_INFO";
const OP_USER_INFO_SUCC = "USER_INFO_SUCC";
const OP_USER_INFO_FAIL = "USER_INFO_FAIL";

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
const VIEWS = ["nickname", "rooms", "match"];
var view = {
	currentView: "nickname",
	
	init: function(){
		// Nickname Insert Event
		document.getElementById("nickname-submit-button").onclick = function() {
			let nicknameInputElement = document.getElementById("nickname-insert-input");
			if(nicknameInputElement.value.length > 0) endpoint.sendUserInfoMessage(nicknameInputElement.value);			
			nicknameInputElement.value = "";
		}
		
		// Rooms Create Event
		document.getElementById("rooms-create-submit").onclick = function() {
			let createNameElement = document.getElementById("rooms-create-name");
			if(createNameElement.value.length > 0) endpoint.sendCreateMessage(createNameElement.value);			
			createNameElement.value = "";
		}
	},
	
	clearRoomList: function(){
		let roomList = document.getElementById("room-list-inner");
		roomList.textContent = "";	// We remove all the childs
	},
	
	makeRoomListEntry: function(name, freeColorInfo, timerInfo) {
		let roomListEntry = document.createElement("div");
		roomListEntry.className = "room-list-entry";
		
		let roomName = document.createElement("p");
		roomName.className = "room-list-entry-name";
		roomName.innerHTML = name;
		roomListEntry.appendChild(roomName);

		let roomTimerInfoContainer = document.createElement("div");
		roomTimerInfoContainer.className = "room-list-entry-timerinfo-container";
		let roomTimerInfo = document.createElement("p");
		roomTimerInfo.className = "room-list-entry-timerinfo";
		roomTimerInfo.innerHTML = timerInfo;
		roomTimerInfoContainer.appendChild(roomTimerInfo);
		roomListEntry.appendChild(roomTimerInfoContainer);
		
		let roomFreeColorInfoContainer = document.createElement("div");
		roomFreeColorInfoContainer.className = "room-list-entry-freecolorinfo-container";
		let roomFreeColorInfo = document.createElement("p");
		roomFreeColorInfo.className = "room-list-entry-freecolorinfo";
		roomFreeColorInfo.innerHTML = (freeColorInfo == "White" ? "♔" : "♚");
		roomFreeColorInfoContainer.appendChild(roomFreeColorInfo);
		roomListEntry.appendChild(roomFreeColorInfoContainer);
		
		let roomJoin = document.createElement("button");
		roomJoin.className = "room-list-entry-join";
		roomJoin.onclick = () => endpoint.sendJoinMessage(name);
		let roomJoinLabel = document.createElement("p");
		roomJoinLabel.className = "room-list-entry-join-label";
		roomJoinLabel.innerHTML = "Join";
		roomJoin.appendChild(roomJoinLabel);
		roomListEntry.appendChild(roomJoin);

		
		document.getElementById("room-list-inner").appendChild(roomListEntry);
	},

	setFeedback: function(feedback) {
		let feedbackElement = null;
		if(view.currentView == "nickname") {
			feedbackElement = document.getElementById("nickname-feedback-text");				
		}
		else if(view.currentView == "rooms") {
			feedbackElement = document.getElementById("rooms-feedback-text");	
		}
		else {
			throw new Error("Can't set feedback in a view that doesn't have feedback text");
		}
		
		if(feedback == "")
			feedbackElement.style.display = "none";
		else {
			feedbackElement.style.display = "block";
			feedbackElement.innerHTML = feedback;
		}
	},

	setCurrentView: function(view) {
		if(!VIEWS.includes(view)) throw new Error("View " + view + " does not exist");
		
		for(let i = 0; i < VIEWS.length; i++) {
			let element = document.getElementById(VIEWS[i]);
			if(VIEWS[i] == view) {
				element.style.display = "flex";
			}
			else {
				element.style.display = "none";				
			}
		}
		this.currentView = view;
	},

	setJoinedRoomInfo: function(room) {
		document.getElementById("match-info-name").innerHTML = "Room - " + room.name;
	}
}

// State
class Room {
	constructor(name, freeColorInfo, timerInfo) {
		this.name = name;
		this.freeColorInfo = freeColorInfo;
		this.timerInfo = timerInfo;
	}
}

var state = {
	nickname: null,
	rooms: [],
	joinedRoomName: null,
	
	init: function() {
		this.nickname = null;
		this.rooms = [];
		this.joinedRoomName = null;
	},
	
	setUserInfo: function(nickname) {
		this.nickname = nickname;
		view.setCurrentView("rooms");
	},
		
	clearRooms: function() {
		this.rooms = [];
		view.clearRoomList();
	},	
	addRoom: function(room) {
		this.rooms.push(room);
		view.makeRoomListEntry(room.name, room.freeColorInfo, room.timerInfo);
	},
	setRooms: function(rooms) {
		this.clearRooms();
		rooms.forEach(room => this.addRoom(new Room(room.name, room.freeColorInfo, room.timerInfo)));
	},
	
	joinRoom: function(roomName) {
		let foundRoom = this.rooms.find(room => room.name == roomName);
		if(foundRoom == undefined) {
			console.log("Can't join room because the room could not be found!");
			return;
		}
		
		this.joinedRoomName = roomName;
		view.setJoinedRoomInfo(foundRoom);
		view.setCurrentView("match");
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
			
			// Op
			switch(op) 
			{
			case OP_USER_INFO_FAIL:
				endpoint.handleUserInfoFailure(tokens[1], tokens[2]);
				break;
			case OP_USER_INFO_SUCC:
				endpoint.handleUserInfoSuccess(tokens[1]);
				break;
			case OP_ROOMS_INFO:
				endpoint.handleRoomsInfo(buildRoomsInfoFromTokens(tokens));
				break;				
			case OP_CREATE_ROOM_FAILURE:
				endpoint.handleCreateRoomFailure(tokens[1], tokens[2]);
				break;
			case OP_CREATE_ROOM_SUCESS:
				let roomName = tokens[1];
				let tunneledRoomsInfo = buildRoomsInfoFromTokens(tokens.slice(2)); // The create rooms success has a tunnelling of the rooms info message, so we extract it
				endpoint.handleCreateRoomSuccess(roomName, tunneledRoomsInfo);
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
	showMessageFeedback: function(feedback) {
		view.setFeedback(feedback);	// The only direct comunication endpoint -> view is here
	},

	handleUserInfoFailure: function(nickname, reason) {
		endpoint.showMessageFeedback("User Info failure of nickname \"" + nickname + "\" because<br>" + reason);
	},
	handleUserInfoSuccess: function(nickname) {
		endpoint.showMessageFeedback("User Info success of nickname \"" + nickname + "\"");
		state.setUserInfo(nickname);
	},
	handleRoomsInfo: function(rooms) {
		state.setRooms(rooms);
	},
	handleCreateRoomFailure: function(roomName, reason) {
		endpoint.showMessageFeedback("Creation failure of room \"" + roomName  + "\" because<br>" + reason);	
	},
	handleCreateRoomSuccess: function(roomName, roomsInfo) {
		endpoint.showMessageFeedback("Creation success of room \"" + roomName + "\"");
		this.handleRoomsInfo(roomsInfo);	// We handle the tunneled rooms info first to avoid problems with the auto join
		state.joinRoom(roomName);	// When we create we auto join that
	},
	handleJoinRoomFailure: function(roomName, reason) {
		endpoint.showMessageFeedback("Join failure of room \"" + roomName  + "\" because<br>" + reason);
	},
	handleJoinRoomSuccess: function(roomName) {
		endpoint.showMessageFeedback("Join success of room \"" + roomName + "\"");
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
	sendUserInfoMessage: function(nickname) {
		this.send(OP_USER_INFO + "|" + nickname);
	},
	sendCreateMessage: function(roomName) {
		this.send(OP_CREATE_ROOM + "|" + roomName);
	},	
	sendJoinMessage: function(roomName) {
		this.send(OP_JOIN_ROOM + "|" + roomName);
	}
}
function buildRoomsInfoFromTokens(tokens) {	// Helper
	let rooms = [];
	for(let t = 1; t + 2 < tokens.length; t += 3) // Foreach token after the first
		rooms.push(new Room(tokens[t], tokens[t + 1], tokens[t + 2])); // We get the room all the info	
	return rooms;
}

view.init();
state.init();
endpoint.connect(WEBSOCKET_HOST);