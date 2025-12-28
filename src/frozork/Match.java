package frozork;

public class Match {
	
	//Constructor
	public Match() {
		board = new Board();
		currentMove = 1;
		currentTurn = false;
		// whiteTimer.start();
		
		running = true;
	}
	
	//Print
	public void print() {
		board.print();
		System.out.println("Move: " + currentMove);
		System.out.println("Turn: " + (currentTurn ? "black" : "white"));
	}
	
	//Debug
	public void printLegalMoves() {
		board.printLegalMoves();
	}
	
	
	
	//Instance variables
	private Board board;
	private int currentMove;
	private boolean currentTurn;	// false white true black
	// private Timer whiteTimer
	// private Timer blackTimer;
	private boolean running;
}
