package comp3170.ass3.sceneobjects;

import comp3170.SceneObject;


import static comp3170.Math.TAU;


public class FanPivot extends SceneObject {
	public static final float FAN_SPIN_SPEED = TAU;
	
	public void update(float dt) {
		getMatrix().rotateZ(FAN_SPIN_SPEED * dt);
	}

}