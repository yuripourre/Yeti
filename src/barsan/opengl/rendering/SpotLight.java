package barsan.opengl.rendering;

import barsan.opengl.math.Vector3;
import barsan.opengl.util.Color;

public class SpotLight extends PointLight {

	private float theta, phi;
	private Vector3 direction;
	
	public SpotLight(Vector3 position, Vector3 direction, float theta, float phi,
			Color diffuse, Color specular) {
		super(position, diffuse, specular);
		
		this.theta = theta;
		this.phi = phi;
		this.direction = direction;
	}
	
	public SpotLight(Vector3 position, Vector3 direction, float theta, float phi) {
		this(position, direction, theta, phi, Color.WHITE, Color.WHITE);
	}
	
	public SpotLight(Vector3 position, Vector3 direction) {
		this(position, direction, 1.0f, 1.0f);
	}

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	public float getPhi() {
		return phi;
	}

	public void setPhi(float phi) {
		this.phi = phi;
	}

	public Vector3 getDirection() {
		return direction;
	}

	public void setDirection(Vector3 direction) {
		this.direction = direction;
	}
}
