package frozork;

public abstract class Piece {
	public Piece(boolean isWhite) {
		this.isWhite = isWhite;
		this.hasMoved = false;
	}
	
	/*public abstract List<Move> pseudoLegalMoves(Board, currentPosition) {
		
	}*/
	
	public abstract void print();
	
	private boolean isWhite;
	private boolean hasMoved;
}
