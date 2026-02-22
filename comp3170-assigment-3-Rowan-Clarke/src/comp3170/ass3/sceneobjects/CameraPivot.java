package comp3170.ass3.sceneobjects;

import comp3170.SceneObject;


import static comp3170.Math.TAU;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import comp3170.InputManager;


public class CameraPivot extends SceneObject {
	final static float ROTATION_SPEED = TAU / 4f;

	
	public void update(float deltaTime, InputManager input) {
		float rotation = 0f;
		if (input.isKeyDown(GLFW_KEY_LEFT)) {
			rotation -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) {
			rotation += ROTATION_SPEED * deltaTime;
		}	
		getMatrix().rotateY(rotation);
	}

}