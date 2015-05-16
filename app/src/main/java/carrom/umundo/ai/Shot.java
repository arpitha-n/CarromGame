package carrom.umundo.ai;

import carrom.umundo.model.geometriccomponents.Vector2f;

public class Shot {
	public float v;
	public Vector2f strikerVelocity;
	public float angle;
	public float strikerX;
	public float strikerY;
	public float diff;

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		Shot other = (Shot) o;
		return other.strikerVelocity.equals(this.strikerVelocity);
	}
}
