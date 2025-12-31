package frozork;

import java.util.List;

public abstract class Piece {
	public Piece(boolean isWhite) {
		this.isWhite = isWhite;
		this.hasMoved = false;
	}
	
	public abstract List<Coord> pseudoLegalMoves(Board board, Coord from);
	
	public abstract void print();
	
	protected boolean isWhite;
	private boolean hasMoved;
}
