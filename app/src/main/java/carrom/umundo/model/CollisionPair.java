package carrom.umundo.model;

/**
 *
 * @author Kapil Ratnani
 *
 */
public class CollisionPair {
	public Piece pieceA;
	public Piece pieceB;

	public CollisionPair(Piece pieceA, Piece pieceB) {
		this.pieceA = pieceA;
		this.pieceB = pieceB;
	}
}
