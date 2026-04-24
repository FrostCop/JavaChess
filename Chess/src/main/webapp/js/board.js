function createSquares() {
	let boardElement = document.getElementById("match-board-inner");
	for(let i = 0; i < 8; i++) {
		for(let j = 0; j < 8; j++) {
			let notation = String.fromCharCode("a".charCodeAt(0) + j) + (8 - i);
			let color = ((i + j) % 2 == 0 ? "light" : "dark");
			
			let square = document.createElement("div");
			square.className = "square " + color;
			square.id = "square" + notation;
			square.innerHTML = notation;
			
			boardElement.appendChild(square);
			
			console.log("Adding square!");
		}
	}
}

createSquares();