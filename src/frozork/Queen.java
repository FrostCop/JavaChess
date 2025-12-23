package frozork;

import java.util.List;

public class Queen extends Piece{
	public Queen(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "Q" : "q");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		return null;
	}
}
