package frozork;

public class Rook extends Piece {
	public Rook(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "♖" : "♜");
	}
}
