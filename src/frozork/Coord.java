package frozork;

public class Coord {
	
	//Constructor
	public Coord(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	//Constructor Helper
	public Coord clone() {
		return new Coord(this.i, this.j);
	}
	
	//Methods
	public boolean isValid() {
		return i >= 0 && i < Board.width && j >= 0 && j < Board.height;
	}
	
	public void plusj() {
		++j;
	}
	
	public void plusi() {
		++i;
	}
	
	public void minusj() {
		--j;
	}
	
	public void minusi() {
		--i;
	}
	
	//Instance variables
	public int i;
	public int j;
}
