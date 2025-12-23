package frozork;

public class Board {
	public Board() {
		cells = new Cell[width][height];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				cells[i][j] = new Cell();
			}
		}
		
		cells[0][0].setPiece(new Rook(false));
		cells[0][1].setPiece(new Knight(false));
		cells[0][2].setPiece(new Bishop(false));
		cells[0][3].setPiece(new King(false));
		cells[0][4].setPiece(new Queen(false));
		cells[0][5].setPiece(new Bishop(false));
		cells[0][6].setPiece(new Knight(false));
		cells[0][7].setPiece(new Rook(false));
		cells[1][0].setPiece(new Pawn(false));
		cells[1][1].setPiece(new Pawn(false));
		cells[1][2].setPiece(new Pawn(false));
		cells[1][3].setPiece(new Pawn(false));
		cells[1][4].setPiece(new Pawn(false));
		cells[1][5].setPiece(new Pawn(false));
		cells[1][6].setPiece(new Pawn(false));
		cells[1][7].setPiece(new Pawn(false));

		cells[7][0].setPiece(new Rook(true));
		cells[7][1].setPiece(new Knight(true));
		cells[7][2].setPiece(new Bishop(true));
		cells[7][3].setPiece(new King(true));
		cells[7][4].setPiece(new Queen(true));
		cells[7][5].setPiece(new Bishop(true));
		cells[7][6].setPiece(new Knight(true));
		cells[7][7].setPiece(new Rook(true));
		cells[6][0].setPiece(new Pawn(true));
		cells[6][1].setPiece(new Pawn(true));
		cells[6][2].setPiece(new Pawn(true));
		cells[6][3].setPiece(new Pawn(true));
		cells[6][4].setPiece(new Pawn(true));
		cells[6][5].setPiece(new Pawn(true));
		cells[6][6].setPiece(new Pawn(true));
		cells[6][7].setPiece(new Pawn(true));
	}
	
	public void print() {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				cells[i][j].print((i + j) % 2 == 0);
			}
			System.out.println();
		}
	}
	
	private Cell[][] cells;
	public static final int width = 8;
	public static final int height = 8;
}
