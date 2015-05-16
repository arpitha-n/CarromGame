package carrom.umundo.ai;

import java.util.Set;

import carrom.umundo.managers.model.Player;
import carrom.umundo.model.Board;
import carrom.umundo.model.Piece;

public abstract class AIPlayer {

	public int difficulty;

	public abstract Shot getShot(Set<Piece> blackPieces,
			Set<Piece> whitePieces, Piece striker, Piece queen, Board board,
			Player aiPlayer);
}
