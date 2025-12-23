package frozork;

import java.util.List;

public class Knight extends Piece {
	public Knight(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "N" : "n");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		return null;
	}
}
