package frozork;

import java.util.List;

public class Pawn extends Piece {
	public Pawn(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "P" : "p");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		return null;
	}
}
