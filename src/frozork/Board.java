package frozork;

public class Board {
	public Board() {
		cells = new Cell[width][height];
	}
	
	public void print() {
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				cells[i][j].print();
			}
			System.out.println();
		}
	}
	
	private Cell[][] cells;
	private final int width = 8;
	private final int height = 8;
}
