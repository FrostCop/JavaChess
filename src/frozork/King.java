package frozork;

import java.util.List;

public class King extends Piece{
	public King(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "K" : "k");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		return null;
	}
}
