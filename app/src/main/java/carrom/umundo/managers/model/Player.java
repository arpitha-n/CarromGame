package carrom.umundo.managers.model;

import carrom.umundo.model.Piece;

public class Player {
	public int id;
	public Piece.PieceType pieceType;
	public boolean scoredQueen;
	public int points;
	public int shootingRectIndex;
	public boolean aiPlayer;
}
