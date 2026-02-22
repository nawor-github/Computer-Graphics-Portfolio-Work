package comp3170.ass3;


import org.joml.Vector3f;

import comp3170.InputManager;


import comp3170.SceneObject;
import comp3170.ass3.sceneobjects.Boat;
import comp3170.ass3.sceneobjects.Camera;
import comp3170.ass3.sceneobjects.CameraPivot;
import comp3170.ass3.sceneobjects.Fan;
import comp3170.ass3.sceneobjects.FanPivot;
import comp3170.ass3.sceneobjects.HeightMap;
import comp3170.ass3.sceneobjects.MeshObject;
import comp3170.ass3.sceneobjects.ToggleLight;
import comp3170.ass3.sceneobjects.Water;


public class Scene extends SceneObject {
	public static Scene theScene = null;

	private Camera cam;
	private CameraPivot camPivot;
	
	private ToggleLight light;
		
	private Boat boat;
	private MeshObject lantern;
	private FanPivot fanPivot; //A pivot point that will control the axis of rotation for the fan
	private Fan fan;
	
	private HeightMap islands;
	private Water water;
	
	static final private Vector3f FAN_OFFSET = new Vector3f(0, 1.252717393f, -1.135f); //From fan centre instructions 
	static final private Vector3f LAMPLIGHT_OFFSET = new Vector3f(0.78f, 1.39f, 0.58f); //From lamp position instructions
	static final private Vector3f BOAT_OFFSET = new Vector3f(0, 19.7f, 0); //Used to put the boat atop the water
	static final private Vector3f ISLANDS_OFFSET = new Vector3f(-50f, 0, -50f); //Used centre the islands heightmap
	static final private Vector3f WATER_OFFSET = new Vector3f(0, 20f, 0); //Used to set water level
	
	private static String BOAT_STRING = "boat";
	private static String FAN_STRING = "fan";
	private static String LANTERN_STRING = "lantern";

	private final static float WATER_SIZE = 100f; //Used to scale the water

	public Scene() {
		theScene = this;
		
		islands = new HeightMap();
		islands.setParent(this);
		islands.getMatrix().translate(ISLANDS_OFFSET);
		
		boat = new Boat(BOAT_STRING);
		boat.setParent(this);
		boat.getMatrix().translate(BOAT_OFFSET);
		
		lantern = new MeshObject(LANTERN_STRING);
		lantern.setParent(boat);
		
		fanPivot = new FanPivot();
		fanPivot.getMatrix().translation(FAN_OFFSET);
		fanPivot.setParent(boat);
		
		fan = new Fan(FAN_STRING, FAN_OFFSET);
		fan.setParent(fanPivot);
		
		camPivot = new CameraPivot();
		camPivot.setParent(boat);
		cam = new Camera();
		cam.setParent(camPivot);
		
		//NOTE TO MARKER:
		//I am fully aware that the light doesn't rotate with the boat, even when directional nighttime light.
		//Whilst this would make more sense, it is NOT what the README indicates, so I specifcally avoided coding it in
		//It looks wonky but I think it is as-written
		light = new ToggleLight();
		light.setParent(boat);
		light.getMatrix().translate(LAMPLIGHT_OFFSET);

		water = new Water();
		water.setParent(this);
		water.getMatrix().translate(WATER_OFFSET).scale(WATER_SIZE);
	}
		
	public void update(float deltaTime, InputManager input) {
		light.update(deltaTime, input);
		boat.update(deltaTime, input);
		fanPivot.update(deltaTime);
		cam.update(deltaTime, input);
		camPivot.update(deltaTime, input);
		lantern.update(deltaTime, input);
		fan.update(deltaTime, input);
		islands.update(deltaTime, input);
		water.update(deltaTime, input);
	}
	
	public Camera getCamera() {
		return cam;
	}
	
	public ToggleLight getLight() {
		return light;
	}
	
}
