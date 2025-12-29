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
		Coord temp = new Coord(from.i, from.j+1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search left
		temp = new Coord(from.i, from.j-1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search up
		temp = new Coord(from.i+1, from.j);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search down
		temp = new Coord(from.i-1, from.j);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search up right
		temp = new Coord(from.i+1, from.j+1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search up left
		temp = new Coord(from.i+1, from.j-1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search down left
		temp = new Coord(from.i-1, from.j-1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		//Search down right
		temp = new Coord(from.i-1, from.j-1);
		if (temp.isValid()) {
			if (board.empty(temp)) {
				result.add(temp.clone());
			}
			else {
				if (board.getPiece(temp).isWhite != this.isWhite) {
					result.add(temp.clone());
				}
			}
		}
		
		return result;
	}
	
}
