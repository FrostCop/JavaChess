package frozork;

public class Tower extends Piece {
	public Tower(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print("R");
	}
}
