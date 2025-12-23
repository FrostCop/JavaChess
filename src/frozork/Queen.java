package frozork;

public class Queen extends Piece{
	public Queen(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "♕" : "♛");
	}
}
