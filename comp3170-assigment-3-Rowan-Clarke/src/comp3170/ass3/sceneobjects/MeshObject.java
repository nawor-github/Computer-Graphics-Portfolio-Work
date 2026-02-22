package comp3170.ass3.sceneobjects;


import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;

import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.ass3.models.MeshData;
import comp3170.ass3.models.MeshData.Mesh;



public class MeshObject extends RenderedObject {

	private static final String OBJ_FILE = "src/comp3170/ass3/models/boat.obj";
	
	static final private String VERTEX_SHADER = "shadedTexturedVertex.glsl";
	static final private String FRAGMENT_SHADER = "shadedTexturedFragment.glsl";
	
	static final private String DIFFUSE = "boat.png";
	private int diffuseID;
			
	Mesh mesh;
	
	public MeshObject(String meshName) {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		// Load the boat OBJ file
		
		MeshData data = null;
		
		try {
			data = new MeshData(OBJ_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

		// Access the vertex and index buffers for the 'boat' submesh:
		try {
			mesh = data.getMesh(meshName);	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			diffuseID = TextureLibrary.instance.loadTexture(DIFFUSE);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (OpenGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	
	int renderMode = GL_FILL;
	int debugMode = 0;

	public void update(float deltaTime, InputManager input) {
		if (input.wasKeyPressed(KeyEvent.VK_B)) { //Toggle wireframe
			if (renderMode == GL_FILL) {
				renderMode = GL_LINE;
			} else {
				renderMode = GL_FILL;
			}
		}
		if (input.wasKeyPressed(KeyEvent.VK_N)) { //Enable normals mode
			debugMode = 1;
		}
		if (input.wasKeyPressed(KeyEvent.VK_M)) { //Enable UVs mode
			debugMode = 2;
		}
		if (input.wasKeyPressed(KeyEvent.VK_V)) { //Disable UVs and normals mode
			debugMode = 0;
		}
	}
	
	@Override
	public void drawSelf(Matrix4f mvpMatrix) {
		vertexBuffer = GLBuffers.createBuffer(mesh.vertices);
		indexBuffer = GLBuffers.createIndexBuffer(mesh.indices);
		normalBuffer = GLBuffers.createBuffer(mesh.normals);
		UVBuffer = GLBuffers.createBuffer(mesh.uvs);
		
		indices = mesh.indices;
		
		shader.enable();

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, diffuseID);
		shader.setUniform("u_texture", 0);

		// turn on trilinear filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		// generate mipmaps
		glGenerateMipmap(GL_TEXTURE_2D);
		super.drawSelf(mvpMatrix);
	}

}
