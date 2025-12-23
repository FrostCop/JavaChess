package frozork;

public class Cell {
	public Cell() {
		
	}
	
	public void print(boolean isOdd) {
		if(piece == null)
			System.out.print(isOdd ? "." : ",");
		else
			piece.print();
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	private Piece piece;
}
