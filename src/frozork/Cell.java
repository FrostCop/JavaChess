package frozork;

public class Cell {
	public void print() {
		if(piece == null)
			System.out.print(" ");
		else
			piece.print();
	}
	
	private Piece piece;
}
