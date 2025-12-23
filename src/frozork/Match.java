package frozork;

public class Match {
	public Match() {
		board = new Board();
		currentMove = 1;
		currentTurn = false;
		// whiteTimer.start();
		
		running = true;
	}
	
	public void print() {
		board.print();
		System.out.println("Move: " + currentMove);
		System.out.println("Turn: " + (currentTurn ? "black" : "white"));
	}
	
	private Board board;
	private int currentMove;
	private boolean currentTurn;	// false white true black
	// private Timer whiteTimer
	// private Timer blackTimer;
	private boolean running;
}
