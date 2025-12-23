package frozork;

public class Bishop extends Piece {
	public Bishop(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print("B");
	}
}
