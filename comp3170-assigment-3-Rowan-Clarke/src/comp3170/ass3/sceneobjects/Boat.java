package comp3170.ass3.sceneobjects;


import java.awt.event.KeyEvent;


import comp3170.InputManager;

import static comp3170.Math.TAU;


public class Boat extends MeshObject {

	public Boat(String meshName) {
		super(meshName);
	}

	public static final float MOVE_SPEED = 3f;
	public static final float TURN_SPEED = TAU / 2f;
	
	@Override
	public void update(float dt, InputManager input) {	
		super.update(dt, input);
		
		if (input.isKeyDown(KeyEvent.VK_A)) { //turn left
			getMatrix().rotateY(TURN_SPEED * dt);
		}
		if (input.isKeyDown(KeyEvent.VK_D)) { //turn right
			getMatrix().rotateY(-TURN_SPEED * dt);
		}
		if (input.isKeyDown(KeyEvent.VK_W)) { //move forwards
			getMatrix().translate(0, 0, MOVE_SPEED * dt);
		}
		if (input.isKeyDown(KeyEvent.VK_S)) { //move backwards
			getMatrix().translate(0, 0, -MOVE_SPEED * dt);
		}
	}
	

}