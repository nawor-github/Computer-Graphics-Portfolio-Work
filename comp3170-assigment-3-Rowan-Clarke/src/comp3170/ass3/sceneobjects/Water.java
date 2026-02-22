package comp3170.ass3.sceneobjects;



import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.ShaderLibrary;
import comp3170.ass3.Scene;

public class Water extends RenderedObject {
	
	static final private String VERTEX_SHADER = "shadedTexturedVertex.glsl";
	static final private String FRAGMENT_SHADER = "waterFragment.glsl";
	
	private long time;


	public Water() {
		time = System.currentTimeMillis();
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		calculateVertices();
		calculateIndices();		
	}
	
	static final private float halfSize = 0.5f;
	
	private void calculateVertices() {
		vertices = new Vector4f[4];
		vertices[0] = new Vector4f(halfSize, 0, halfSize, 1f);
		vertices[1] = new Vector4f(halfSize, 0, -halfSize, 1f);
		vertices[2] = new Vector4f(-halfSize, 0, -halfSize, 1f);
		vertices[3] = new Vector4f(-halfSize, 0, halfSize, 1f);
		
		vertexBuffer = GLBuffers.createBuffer(vertices);
		
		UVs = new Vector2f[4];
		UVs[0] = new Vector2f(0,0);
		UVs[1] = new Vector2f(0,1f);
		UVs[2] = new Vector2f(1f,1f);
		UVs[3] = new Vector2f(1f,0);
		UVBuffer = GLBuffers.createBuffer(UVs);
		
		normals = new Vector4f[4];
		for (int i = 0; i < 4; i++) {
			normals[i] = new Vector4f(0,1f,0,0);
		}
		normalBuffer = GLBuffers.createBuffer(normals);
	}
	
	private void calculateIndices() {
		indices = new int[6];
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		
		indices[3] = 0;
		indices[4] = 2;
		indices[5] = 3;
		
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	private Vector4f camPosition = new Vector4f();

	private static final Vector3f SPEC_MATERIAL = new Vector3f(1f,1f,1f);
	private static final Vector4f WATER_COLOUR = new Vector4f(0f,0.75f,1f,0.5f);

	private float specularity = 10;
	
	@Override
	public void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();

		shader.setUniform("u_specularMaterial", SPEC_MATERIAL);
		shader.setUniform("u_waterColour", WATER_COLOUR);
		shader.setUniform("u_time", (float) (System.currentTimeMillis()-time)/1000f);

		Camera camera = Scene.theScene.getCamera();
		shader.setUniform("u_camPosition", camera.getPosition(camPosition));

		shader.setUniform("u_specularity", specularity);
		
		super.drawSelf(mvpMatrix);
	}
}
