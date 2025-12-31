package frozork;

public class Cell {
	
	public void print(boolean isOdd) {
		if(piece == null)
			System.out.print(isOdd ? "." : ",");
		else 
			piece.print();
		
		System.out.print(" ");
	}
	
	public Piece getPiece() {
		return piece;
	}	
	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	private Piece piece;
}
