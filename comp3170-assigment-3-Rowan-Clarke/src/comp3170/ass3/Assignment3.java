package comp3170.ass3;

import static org.lwjgl.opengl.GL11.GL_BLEND;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import static org.lwjgl.opengl.GL11.glViewport;

import java.io.File;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import comp3170.IWindowListener;
import comp3170.InputManager;
import static org.lwjgl.glfw.GLFW.*;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.Window;
import comp3170.ass3.sceneobjects.Camera;

public class Assignment3 implements IWindowListener {

	public static Assignment3 theWindow;
	private static final File SHADER_DIR = new File("src/comp3170/ass3/shaders");
	private static final File TEXTURE_DIR = new File("src/comp3170/ass3/textures");

	private Window window;
	private int screenWidth = 800;
	private int screenHeight = 800;
	private Scene scene;

	private InputManager input;
	private long oldTime;
	
	Camera camera;
	
	private Vector4f skyColour = new Vector4f(0.5f, 0.8f, 1f, 1f);
	private Vector4f nightColour = new Vector4f(0, 0, 0, 0);

	private static final int SAMPLE_NUM = 4; //Number of samples per pixel

	public Assignment3() throws OpenGLException {
		window = new Window("Assignment3", screenWidth, screenHeight, this);
		//I spent so long trying to do this myself before realising the custom window class was undoing my window hints. :(
		window.setSamples(SAMPLE_NUM); //enables MSAA
		window.setResizable(true); //Lets the window be resized
		window.run();		
	}


	@Override
	public void init() {
		setClearColour(skyColour);
		new ShaderLibrary(SHADER_DIR);
		new TextureLibrary(TEXTURE_DIR);
		
		glEnable(GL_MULTISAMPLE); //enable multisampling for our anti-aliasing

		
		glEnable(GL_DEPTH_TEST); // enable depth testing

		glEnable(GL_BLEND); //enable alpha blending
		
		// set the blend function to
		// c = a * c_new + (1-a) c_old
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
		
		
		glEnable(GL_CULL_FACE); // enable backface culling
		glCullFace(GL_BACK); 
		glFrontFace(GL_CCW);
		
		scene = new Scene();
		camera = scene.getCamera();
		
		// initialise oldTime
		input = new InputManager(window);
		oldTime = System.currentTimeMillis();
	}
	
	private void setClearColour(Vector4f colour) {
		glClearColor(colour.x, colour.y, colour.z, colour.w);
	}

	private Vector4f currentColour = skyColour;

	private void update() {
		long time = System.currentTimeMillis();
		float deltaTime = (time - oldTime) / 1000f;
		oldTime = time;
		
		// update the scene
		scene.update(deltaTime, input);
		
		if (input.wasKeyPressed(GLFW_KEY_P)) { //Toggle between night and day
			if (currentColour == skyColour) {
				setClearColour(nightColour);
				currentColour = nightColour;
			} else {
				setClearColour(skyColour);
				currentColour = skyColour;
			}
			scene.getLight().toggle();
		}
		// clear the input at the end of each update
		input.clear();
	}
	
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f mvpMatrix = new Matrix4f();

	@Override
	public void draw() {		
		camera.getViewMatrix(viewMatrix);
		camera.getProjectionMatrix(projectionMatrix);
		mvpMatrix.set(projectionMatrix).mul(viewMatrix);
		update();
		
		glClear(GL_COLOR_BUFFER_BIT);	
		
		glClearDepth(1); // sets clear depth to 1
		glClear(GL_DEPTH_BUFFER_BIT); //clears the depth buffer for each frame
		
		scene.draw(mvpMatrix);
	}

	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		glViewport(0, 0, width, height);
		camera.setAspect(width, height);
	}

	@Override
	public void close() {
	}

	public static void main(String[] args) throws OpenGLException {
		new Assignment3();
	}
}
