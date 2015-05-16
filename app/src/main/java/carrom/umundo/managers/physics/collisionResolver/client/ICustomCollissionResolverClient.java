package carrom.umundo.managers.physics.collisionResolver.client;

import carrom.umundo.model.Piece;

/**
 * Client should implement this interface to get
 * collision event notification
 * @author theripper
 *
 */
public interface ICustomCollissionResolverClient {
	public void collisionHappened(Piece pieceA, Piece pieceB);
}
