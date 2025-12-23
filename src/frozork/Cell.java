package frozork;

public class Cell {
	public Cell() {
		
	}	
	public Cell(Piece piece) {
		this.piece = piece;
	}
	
	public void print() {
		if(piece == null)
			System.out.print(" ");
		else
			piece.print();
	}
	
	private Piece piece;
}
