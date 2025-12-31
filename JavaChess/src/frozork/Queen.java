package frozork;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece{
	public Queen(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "Q" : "q");
	}
	
	@Override
	public List<Coord> pseudoLegalMoves(Board board, Coord from) {
	ArrayList<Coord> result  = new ArrayList<>();
	//Search right
		for (Coord temp = new Coord(from.i, from.j+1); 
				temp.isValid(); ++temp.j) {
				if (board.empty(temp)) {
					result.add(temp.clone());
				}
				else {
					if (board.getPiece(temp).isWhite != this.isWhite) {
						result.add(temp.clone());
					}
					break;
				}
		}
		
		//Search left
		for (Coord temp = new Coord(from.i, from.j-1); 
				temp.isValid(); --temp.j) {
				if (board.empty(temp)) {
					result.add(temp.clone());
				}
				else {
					if (board.getPiece(temp).isWhite != this.isWhite) {
						result.add(temp.clone());
					}				
					break;
				}
		}
		
		//Search up
		for (Coord temp = new Coord(from.i+1, from.j); 
				temp.isValid(); ++temp.i) {
				if (board.empty(temp)) {
					result.add(temp.clone());
				}
				else {
					if (board.getPiece(temp).isWhite != this.isWhite) {
						result.add(temp.clone());
					}				
					break;
				}
		}
		
		
		//Search down
		for (Coord temp = new Coord(from.i-1, from.j); 
				temp.isValid(); --temp.i) {
				if (board.empty(temp)) {
					result.add(temp.clone());
				}
				else {
					if (board.getPiece(temp).isWhite != this.isWhite) {
						result.add(temp.clone());
					}				
					break;
				}
		}
		
		//Search up right
		for (Coord temp = new Coord(from.i+1, from.j+1); 
				temp.isValid(); ++temp.i, ++temp.j) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
				break;
			}	
		}
		
		//Search up left
		for (Coord temp = new Coord(from.i+1, from.j-1); 
				temp.isValid(); ++temp.i, --temp.j) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
				break;
			}	
		}
		
		//Search bottom left
		for (Coord temp = new Coord(from.i-1, from.j-1); 
				temp.isValid(); --temp.i, --temp.j) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
				break;
			}	
		}
		
		//Search bottom right
				for (Coord temp = new Coord(from.i-1, from.j+1); 
						temp.isValid(); --temp.i, ++temp.j) {
					if (board.empty(temp)) {
						result.add(temp.clone());
					}
					else {
						if (board.getPiece(temp).isWhite != this.isWhite) {
							result.add(temp.clone());
						}
						break;
					}	
				}
			
		return result;
	}
}
