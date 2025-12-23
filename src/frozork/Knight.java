package frozork;

public class Knight extends Piece {
	public Knight(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print("N");
	}

}
