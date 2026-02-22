package comp3170.ass3.sceneobjects;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.SceneObject;
import comp3170.InputManager;

import static comp3170.Math.TAU;

public class ToggleLight extends SceneObject  { //Class that handles both directional and point lights. 
	//Combined as directional/source vectors are used to pass directional information for point lights for directionality

	static final public String VERTEX_SHADER = "simpleVertex.glsl";
	static final public String FRAGMENT_SHADER = "simpleFragment.glsl";
	
	// Variables for actual light elements
	public Vector4f direction = new Vector4f(0f,0.8f,1f,0);
	public Vector4f position = new Vector4f();
	
	public ToggleLight() {
	}
	
	private static final Vector3f DAY_INTENSITY = new Vector3f(1.0f,1.0f,1.0f); // Pure white
	private static final Vector3f DAY_AMBIENT = new Vector3f(0.3f,0.3f,0.3f); // dim white
	
	private static final Vector3f NIGHT_INTENSITY = new Vector3f(1.0f,1.0f,0f); // Pure yellow
	private static final Vector3f NIGHT_AMBIENT = new Vector3f(0.01f,0.01f,0.1f); // very dark blue
	
	public Vector3f intensity = DAY_INTENSITY;
	public Vector3f ambient = DAY_AMBIENT;
	
	private boolean isDirectional = true;
	public void toggle() { //Switches from night to day mode
		if (isDirectional) {
			intensity = NIGHT_INTENSITY;
			ambient = NIGHT_AMBIENT;
			isDirectional = false;
		} else {
			intensity = DAY_INTENSITY;
			ambient = DAY_AMBIENT;
			isDirectional = true;
		}
	}
	
	public Vector4f getSourceVectorOrPosition(Vector4f dest) {
		if (isDirectional) {
			return dest.set(direction);
		} else {
			Matrix4f modelMatrix = new Matrix4f();
			Vector3f position = new Vector3f();
			getModelToWorldMatrix(modelMatrix);
			position = modelMatrix.getTranslation(position);
			return dest.set(position, 1);			
		}
	}
	
	public Vector4f getDirectionVector(Vector4f dest) {
		return getMatrix().getColumn(2, dest);
		//return dest.set(direction);
	}
	
	public Vector4f getPosition(Vector4f dest) {
		Matrix4f modelMatrix = new Matrix4f();
		Vector3f position = new Vector3f();
		getModelToWorldMatrix(modelMatrix);
		position = modelMatrix.getTranslation(position);
		return dest.set(position, 1);
	}
	
	public boolean isDirectional () {
		return isDirectional;
	}
	public Vector3f getIntensity(Vector3f dest) {
		return intensity.get(dest);
	}

	public Vector3f getAmbient(Vector3f dest) {
		return dest.set(ambient);
	}
	
	private final static float ROTATION_SPEED = TAU / 4;

	public void update(float deltaTime, InputManager input) {

		if (input.isKeyDown(GLFW_KEY_LEFT_BRACKET)) {
			getMatrix().rotateY(ROTATION_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT_BRACKET)) {
			getMatrix().rotateY(-ROTATION_SPEED * deltaTime);
		}
	}
}