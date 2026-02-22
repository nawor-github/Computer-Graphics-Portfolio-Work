package comp3170.ass3.sceneobjects;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.OpenGLException;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;

public class HeightMap extends RenderedObject {

	private static final File HEIGHT_MAP = new File("src/comp3170/ass3/maps/islands.png");
	private int width;
	private int depth;
	private float[][] heights;

	static final private String VERTEX_SHADER = "shadedTexturedVertex.glsl";
	static final private String FRAGMENT_SHADER = "HeightMapFragment.glsl";
	
	static final private String GRASS_TEXTURE = "terrain-grass.png";
	static final private String SAND_TEXTURE = "terrain-sand.png";
	
	private int grassID;
	private int sandID;
	
	private static final float BLEND_AMOUNT = 4f; //Amount of space (in m) to apply blending accross
	static final private float BLEND_HEIGHT = 18f; //Line at which to blend above
	

	public HeightMap() {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		loadHeights(HEIGHT_MAP); // load the vertex heights from the image

		calculatePerVertexInfo(); // calculate vertices, normals and UVs
		calculateIndices();	// calculate indices
		
		try {
			grassID = TextureLibrary.instance.loadTexture(GRASS_TEXTURE);
			sandID = TextureLibrary.instance.loadTexture(SAND_TEXTURE);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (OpenGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static float DISTANCE_BETWEEN_POINTS = 1f;
	private static float HEIGHT_EXAGGERATION = 50f;
	
	private void calculatePerVertexInfo() { // calculate vertices, normals and UVs
		vertices = new Vector4f[width * depth];
		UVs = new Vector2f[width * depth];
		normals = new Vector4f[width * depth];

		int index = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < depth; j++) {
				float height = heights[i][j];
				vertices[index] = getPoint(height, i, j); //vertices
				
				UVs[index] = new Vector2f(i,j); //UVs
				
				Vector4f[][] points3x3 = get3x3(i, j);
				
				normals[index] = calculateNormal(points3x3); //normals
				
				index++;
			}
		}
		vertexBuffer = GLBuffers.createBuffer(vertices);
		UVBuffer = GLBuffers.createBuffer(UVs);
		normalBuffer = GLBuffers.createBuffer(normals);
	}
	
	private void calculateIndices() {
		indices = new int[(width - 1) * (depth - 1) * 6]; //6 indices per tri, 2 tris per square, one less square per row/coloumn than the width
		int index = 0;
		for (int z = 0; z < depth-1; z++) {
			int rowStartIndex = width * z; //Index at the beggining of the row. For a 3x3, 0 for first row, then 3
			int nextRowStartIndex = width * (z+1); //Index at the beggining of the next row. For a 3x3, 3 for first row, then 6
			for (int x = 0; x < width-1; x++) {
				indices[index] = rowStartIndex + x; //Top left of TRI 1
				indices[index+1] = rowStartIndex + x + 1; //Top right of TRI 1
				indices[index+2] = nextRowStartIndex + x; //Bottom left of TRI 1
				
				indices[index+3] = rowStartIndex + x + 1; //Top right of TRI 2
				indices[index+4] = nextRowStartIndex + x + 1; //Bottom right of TRI 2
				indices[index+5] = nextRowStartIndex + x; // Bottom left of TRI 2
				index+=6;
			}
		}
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}	
	
	@Override
	public void drawSelf(Matrix4f mvpMatrix) {	
		shader.enable();

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // in S (i.e. U)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // is T (i.e. V)

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, grassID);
		shader.setUniform("u_grassTexture", 0);
		
		// turn on trilinear filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		// generate mipmaps
		glGenerateMipmap(GL_TEXTURE_2D);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // in S (i.e. U)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // is T (i.e. V)
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, sandID);
		shader.setUniform("u_sandTexture", 1);
		
		// turn on trilinear filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		// generate mipmaps
		glGenerateMipmap(GL_TEXTURE_2D);


		shader.setUniform("u_blendHeight", BLEND_HEIGHT);
		shader.setUniform("u_blendAmount", BLEND_AMOUNT);
		
		super.drawSelf(mvpMatrix);
	}

	/**
	 * Load the height data. This sets the values of the fields:
	 *
	 *    (width, depth) are set to the (x,y) image size, 
	 *    which should equal the number of vertices in each direction
	 *    
	 *    heights[i][j] is set to the value of the pixel at coordinates (i,j).
	 *    This is a float between 0 and 1, corresponding to the minimum and maximum height values. 
	 * 
	 * @param imageFile
	 */
	
	private void loadHeights(File imageFile) {
		BufferedImage mapImage;
		try {
			mapImage = ImageIO.read(imageFile);

			width = mapImage.getWidth();
			depth = mapImage.getHeight();

			heights = new float[width][depth];
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < depth; z++) {
					int rgb = mapImage.getRGB(x, z);
					int r = (rgb & 0xff0000) >> 16;
					int g = (rgb & 0xff00) >> 8;
					int b = rgb & 0xff;

					// scale to [0...1]
					heights[x][z] = (r + g + b) / 255f / 3;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Vector4f getPoint (float height, int i, int j) { //Calculates a point using the indices and height
		return new Vector4f(
				DISTANCE_BETWEEN_POINTS * i,
				HEIGHT_EXAGGERATION * height,
				DISTANCE_BETWEEN_POINTS * j,
				1f
				);
	}
	
	private Vector4f calculateNormal (Vector4f[] [] p) { //used to calculate the normal using 2 tangents and the 3x3 of adjacent points
		Vector4f tangentX = p[2][1].sub(p[0][1]);
		Vector4f tangentZ = p[1][2].sub(p[1][0]);
		Vector4f rawNormal = cross(tangentZ, tangentX);
		return rawNormal.normalize();
	}
	
	private Vector4f[][] get3x3 (int i, int j){ //Produces a 2D array of adjacent points, assuming flatness when edges are exceeded
		Vector4f[][] result = new Vector4f[3][3]; //we makin' a 3x3
		for (int x = -1; x < 2; x++) { //want to draw vertices from all adjacent spaces
			for (int y = -1; y < 2; y++) {
				int xCoord = i+x;
				int yCoord = j+y;
				//These tests makes it so that the program assumes that points continue flat at the edge of the mesh for the purposes of calcing normals
				if (xCoord < 0) { 
					xCoord = 0;
				}
				if (xCoord >= width) {
					xCoord = width - 1;
				}
				if (yCoord < 0) { 
					yCoord = 0;
				}
				if (yCoord >= depth) {
					yCoord = depth - 1;
				}
				result[x+1][y+1] = getPoint(heights[xCoord][yCoord], xCoord, yCoord);
			}
		}
		return result;
	}
	
	private Vector4f cross (Vector4f a, Vector4f b) { //Wrote this as couldn't find inbuilt cross product in Vector4f
		float x = (a.y * b.z) - (a.z * b.y);
		float y = (a.z * b.x) - (a.x * b.z);
		float z = (a.x * b.y) - (a.y * b.x);
		return new Vector4f(x,y,z,0);
	}
	

}
