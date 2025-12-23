package frozork;

public class Pawn extends Piece {
	public Pawn(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print("R");
	}
}
