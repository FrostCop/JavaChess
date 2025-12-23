package frozork;

import java.util.List;

public class Rook extends Piece {
	public Rook(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "R" : "r");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		return null;
	}
}
