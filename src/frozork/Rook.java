package frozork;

import java.util.ArrayList;

public class Rook extends Piece {
	public Rook(boolean isWhite) {
		super(isWhite);
	}
	
	@Override
	public void print() {
		System.out.print(isWhite ? "R" : "r");
	}
	
	@Override
	public ArrayList<Coord> pseudoLegalMoves(Board board, Coord from) {
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
		
		return result;
	
	}

}