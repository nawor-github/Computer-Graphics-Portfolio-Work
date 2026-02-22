package comp3170.ass3.sceneobjects;

import org.joml.Matrix4f;


import org.joml.Vector4f;

import comp3170.InputManager;
import comp3170.SceneObject;

import static org.lwjgl.glfw.GLFW.*;
import static comp3170.Math.TAU;

public class Camera extends SceneObject {
	
	private static final float NEAR = 0.1f;
	private static final float FAR = 100f;
	private static final float ELEVATION = 1.5f;
	private static final float START_DISTANCE = 4.5f;
	
	private float aspect = 1;

	float fovy = TAU / 4f; //Initiliases the starting FOV

	
	
		
	public Camera() {
		getMatrix().rotateY(TAU/2f).translate(0.0f,ELEVATION,START_DISTANCE); //Flip camera around and translate to initial position
	}
	
	public void setAspect(float width, float height) {
		aspect = width/height;
	}
	
	public Matrix4f getViewMatrix(Matrix4f dest) {
		Matrix4f modelToWorldMatrix = new Matrix4f();
		getModelToWorldMatrix(modelToWorldMatrix);
		return modelToWorldMatrix.invert(dest);
	}
	
	public Matrix4f getProjectionMatrix(Matrix4f dest) {
		return dest.setPerspective(fovy, aspect, NEAR, FAR);
	}
	
	public Vector4f getPosition(Vector4f dest) {
		// for a perspective camera
		// it is nearly always useful to treat the view vector as the origin point of the cameraMatrix
		Matrix4f modelToWorldMatrix = new Matrix4f();
		getModelToWorldMatrix(modelToWorldMatrix);
		return modelToWorldMatrix.getColumn(3, dest);
	}
	
	final static float TILT_LIMIT = TAU / 4;
	final static float MAX_DOLLY = 10f;
	final static float MIN_DOLLY = 2f;
	final static float MIN_FOV = TAU / 8;
	final static float MAX_FOV = TAU / 3;
	
	final static float ROTATION_SPEED = TAU / 4f;
	final static float ZOOM_SPEED = TAU / 4f;
	final static float DOLLY_SPEED = 8f;

	private float distance = START_DISTANCE;
	private float angle = 0;
	private float currentTilt = 0f;
	private float currentDistance = distance;
	
	public void update(float deltaTime, InputManager input) {
		distance = 0;
		angle = 0;
		float correctedTiltSpeed = ROTATION_SPEED * deltaTime;

		
		if (input.isKeyDown(GLFW_KEY_UP)) { //Tilting upwards
			angle -= correctedTiltSpeed;
			currentTilt -= correctedTiltSpeed;
			if (currentTilt <= -TILT_LIMIT) {
				angle = 0;
				currentTilt += correctedTiltSpeed;
			}
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) { //Tilting down
			angle += correctedTiltSpeed;
			currentTilt += correctedTiltSpeed;
			if (currentTilt >= TILT_LIMIT) {
				angle = 0;
				currentTilt -= correctedTiltSpeed;
			}
		}

		float correctedSpeed = DOLLY_SPEED * deltaTime;

		if (input.isKeyDown(GLFW_KEY_PAGE_UP)) { //Dollying forwards
			distance += correctedSpeed;
			currentDistance += correctedSpeed;
			if (currentDistance >= MAX_DOLLY) {
				currentDistance -= correctedSpeed;
				distance = 0;
			}
		}
		if (input.isKeyDown(GLFW_KEY_PAGE_DOWN)) { //Dollying backwards
			distance -= correctedSpeed;
			currentDistance -= correctedSpeed;
			if (currentDistance <= MIN_DOLLY) {
				currentDistance += correctedSpeed;
				distance = 0;
			}
		}
		if (input.isKeyDown(GLFW_KEY_COMMA)) { //Zooming that ish in crazy style
			fovy += ZOOM_SPEED * deltaTime;
			if (fovy >= MAX_FOV) {
				fovy = MAX_FOV;
			}
		}
		if (input.isKeyDown(GLFW_KEY_PERIOD)) { //Zooming that ish out, normal style
			fovy -= ZOOM_SPEED * deltaTime;
			if (fovy <= MIN_FOV) {
				fovy = MIN_FOV;
			}
		}
		getMatrix().rotateX(angle).translate(0,0,distance);
		
		
	}
}
