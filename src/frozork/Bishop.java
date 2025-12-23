package frozork;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
	public Bishop(boolean isWhite) {
		super(isWhite);
	}
	

	@Override
	public void print() {
		System.out.print(isWhite ? "B" : "b");
	}

	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
		ArrayList<Coord> result = new ArrayList<Coord>();
		
		for (Coord temp = new Coord(from.i+1, from.j+1); 
				temp.isValid() && board.empty(temp); 
				++temp.i, ++temp.j) {
			result.add(temp.clone());
		}
		
		for (Coord temp = new Coord(from.i-1, from.j-1); 
				temp.isValid() && board.empty(temp); 
				--temp.i, --temp.j) {
			result.add(temp.clone());
		}
		
		for (Coord temp = new Coord(from.i+1, from.j-1); 
				temp.isValid() && board.empty(temp); 
				++temp.i, --temp.j) {
			result.add(temp.clone());
		}
		
		for (Coord temp = new Coord(from.i-1, from.j+1); 
				temp.isValid() && board.empty(temp); 
				--temp.i, ++temp.j) {
			result.add(temp.clone());
		}
		
		return result;
	}
	
	
}
