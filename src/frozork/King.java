package frozork;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
	public King(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "K" : "k");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
ArrayList<Coord> result  = new ArrayList<>();
		
		//Search right
		for (Coord temp = new Coord(from.i, from.j+1); 
				temp.isValid() && board.empty(temp); 
				++temp.j) {
			result.add(temp.clone());
			break;
		}
		
		//Search left
		for (Coord temp = new Coord(from.i, from.j-1); 
				temp.isValid() && board.empty(temp); 
				--temp.j) {
			result.add(temp.clone());
			break;
		}
		
		//Search up
		for (Coord temp = new Coord(from.i+1, from.j); 
				temp.isValid() && board.empty(temp); 
				++temp.i) {
			result.add(temp.clone());
			break;
		}
		
		//Search down
		for (Coord temp = new Coord(from.i+-1, from.j); 
				temp.isValid() && board.empty(temp); 
				--temp.i) {
			result.add(temp.clone());
			break;
		}
		
		return result;
	}
	
}
