//const WEBSOCKET_HOST = "ws://127.0.0.1:8080/Chess/websocket/server"
const WEBSOCKET_HOST = "ws://147.53.255.127:8080/Chess/websocket/server"

const OP_ROOMS_INFO = "ROOMS_INFO";

const OP_CREATE_ROOM = "CREATE";
const OP_CREATE_ROOM_SUCESS = "CREATE_SUCC";
const OP_CREATE_ROOM_FAILURE = "CREATE_FAIL";

const OP_JOIN_ROOM = "JOIN";
const OP_JOIN_ROOM_SUCCESS = "JOIN_SUCC";
const OP_JOIN_ROOM_FAILURE = "JOIN_FAIL";

function clearRoomList() {
	let roomList = document.getElementById("room-list");
	roomList.textContent = "";	// We remove all the childs
}
function makeRoomListEntry(name, connectionsCount) {
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
	roomJoin.onclick = () => rooms.sendJoinMessage(name);
	let roomJoinLabel = document.createElement("p");
	roomJoinLabel.className = "room-list-entry-join-label";
	roomJoinLabel.innerHTML = "Join";
	roomJoin.appendChild(roomJoinLabel);
	roomListEntry.appendChild(roomJoin);

	
	document.getElementById("room-list").appendChild(roomListEntry);
}

function setFeedback(feedback) {
	let feedbackElement = document.getElementById("feedback");
	if(feedback == "")
		feedbackElement.style.display = "none";
	else {
		feedbackElement.style.display = "block";
		feedbackElement.innerHTML = feedback;
	}
}

var rooms = {

    socket: null,

    connect: function(host) {

        this.socket = new WebSocket(host);

        this.socket.onopen = function() {
			console.log("Socket Opened");
			
			// Create Event
            document.getElementById("create-submit").onclick = function() {
				var createNameElement = document.getElementById("create-name");
				rooms.sendCreateMessage(createNameElement.value);
				createNameElement.value = "";
			}
        }

        this.socket.onclose = function() {
			console.log("Socket Closed");

			// Remove Create Event
			document.getElementById("create-submit").onclick = null;
        }

        this.socket.onmessage = function(message) {
			console.log("Socket Message " + message.data);
			
			const tokens = message.data.split("|");
			
			let op = tokens[0];
			let failRoomName;
			let failReason;
			let successRoomName;
			switch(op) 
			{
			case OP_ROOMS_INFO:
				// We got the Rooms Info. So we reset the room list and add all the entries we've got
				clearRoomList();
				for(let t = 1; t + 1 < tokens.length; t += 2) {	// Foreach token after the first
					// We get the room name and the room connections count
					let infoRoomName = tokens[t];
					let infoRoomConnectionsCount = tokens[t + 1];
					makeRoomListEntry(infoRoomName, infoRoomConnectionsCount);
				}
				break;
				
			case OP_CREATE_ROOM_FAILURE:
				failRoomName = tokens[1];
				failReason = tokens[2];
				setFeedback("Creation failure of room \"" + failRoomName  + "\" because<br>" + failReason);
				break;
				
			case OP_CREATE_ROOM_SUCESS:
				successRoomName = tokens[1];				
				setFeedback("Creation success of room \"" + successRoomName + "\"");
				break;
				
			case OP_JOIN_ROOM_FAILURE:
				failRoomName = tokens[1];
				failReason = tokens[2];
				setFeedback("Join failure of room \"" + failRoomName  + "\" because<br>" + failReason);
				break;
				
			case OP_JOIN_ROOM_SUCCESS:
				successRoomName = tokens[1];				
				setFeedback("Join success of room \"" + successRoomName + "\"");
				break;
			}
        }
    },

	sendCreateMessage: function(roomName) {
		rooms.socket.send(OP_CREATE_ROOM + "|" + roomName);
	},
	
	sendJoinMessage: function(roomName) {
		rooms.socket.send(OP_JOIN_ROOM + "|" + roomName);
	}
}

rooms.connect(WEBSOCKET_HOST);