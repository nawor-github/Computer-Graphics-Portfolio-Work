package comp3170.ass3.sceneobjects;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class Fan extends MeshObject {
	
	static private Vector3f fanOffset;
	
	public Fan(String meshName, Vector3f modelOffset) {
		super(meshName);
		fanOffset = modelOffset;
		correctPosition();
	}
	
	private void correctPosition() {
		Matrix4f correctionMatrix = new Matrix4f();
		correctionMatrix.translation(fanOffset);
		correctionMatrix.invert();
		
		getMatrix().mul(correctionMatrix);
	}

}