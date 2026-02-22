package comp3170.ass3.sceneobjects;


import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.awt.event.KeyEvent;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ass3.Scene;

public class RenderedObject extends SceneObject {
	
	protected Vector4f[] vertices;
	protected int vertexBuffer;
	
	protected Vector4f[] normals;
	protected int normalBuffer;
	
	protected int[] indices;
	protected int indexBuffer;
	
	protected Vector2f[] UVs;
	protected int UVBuffer;
	
	protected Shader shader;
	
	int renderMode = GL_FILL; //default render mode (switches to GL_LINE when wireframe)
	int debugMode = 0; //int used to switch between 3 modes in shader. 
	// 0 = standard rendering
	// 1 = normals mode
	// 2 = UVs mode
	
	//It has been a very deliberate choice to not split the fragments into seperate UV and Normal fragment shaders
	//This is due to the water shader calculating normals per fragment in the fragment shader
	//I want the Normal debug view to be useful, so it must use the ACTUAL normals, not a copy (as this is an easy source of issues)
	//So it seems better to just include the switch in every of the 3 fragments
	//Rather than maintaining complex normal code in a seperate waterNormal fragment shader
	//And having to also switch between shaders in every rendered class

	public RenderedObject() {
	}

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
	
	protected Matrix4f modelMatrix = new Matrix4f();
	protected Matrix4f normalMatrix = new Matrix4f();
	protected Vector4f lightDirection = new Vector4f();
	protected Vector3f lightIntensity = new Vector3f();
	protected Vector3f ambientIntensity = new Vector3f();
	
	@Override
	public void drawSelf(Matrix4f mvpMatrix) {
		getModelToWorldMatrix(modelMatrix);
		
		//Set universal uniforms
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_modelMatrix", modelMatrix);
		shader.setUniform("u_normalMatrix", modelMatrix.normal(normalMatrix));
		
		ToggleLight light = Scene.theScene.getLight();
		if (light.isDirectional()) {
			shader.setUniform("u_lightDirection", light.getDirectionVector(lightDirection));
			shader.setUniform("u_lightPosition", new Vector4f(0));
		} else {
			shader.setUniform("u_lightDirection", light.getDirectionVector(lightDirection));
			shader.setUniform("u_lightPosition", light.getPosition(lightDirection));
		}

		shader.setUniform("u_intensity", light.getIntensity(lightIntensity));
		shader.setUniform("u_ambientIntensity", light.getAmbient(ambientIntensity));
		
		shader.setUniform("u_mode", debugMode);
		
		//Set universal attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_normal", normalBuffer);
		shader.setAttribute("a_texcoord", UVBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, renderMode); //Controls wireframe vs normal rendering
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		//There is an image about the draw call irrevocably burnt in my mind. It's in the images folder of this repo. 
		//I think of it everytime i think about the draw call
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0); //GPU, render this object
	}
}
