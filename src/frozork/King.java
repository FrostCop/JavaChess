package frozork;

public class King extends Piece{
	public King(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "♔" : "♚");
	}
}
