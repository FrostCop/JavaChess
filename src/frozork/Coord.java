package frozork;

public class Coord {
	
	public Coord(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	public Coord clone() {
		return new Coord(this.i, this.j);
	}
	
	public boolean isValid() {
		return i >= 0 && i < Board.width && j >= 0 && j < Board.height;
	}
	public int i;
	public int j;
}
