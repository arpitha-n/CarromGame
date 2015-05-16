package carrom.umundo.managers.clients;

import java.util.List;

import carrom.umundo.model.CollisionPair;

/**
 * notifies client of physics events All motion stopped event
 * 
 * @author theripper
 * 
 */
public interface IPhysicsManagerClient {
	public void allMotionStopped(List<CollisionPair> collisionPairs);
}
