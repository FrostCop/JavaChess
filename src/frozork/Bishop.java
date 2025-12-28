package frozork;

import java.util.ArrayList;

public class Bishop extends Piece {
	public Bishop(boolean isWhite) {
		super(isWhite);
	}
	

	@Override
	public void print() {
		System.out.print(isWhite ? "B" : "b");
	}
	
	//
	@Override
	public ArrayList<Coord> pseudoLegalMoves(Board board, Coord from) {
		ArrayList<Coord> result = new ArrayList<Coord>();
		
		
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
