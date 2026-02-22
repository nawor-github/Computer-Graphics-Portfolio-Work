#version 410

in vec4 a_position;		// vertex in 3D homogenous coordinates (MODEL)
in vec4 a_normal;		// normal vector in 3D homogenous coordinates (MODEL)
in vec2 a_texcoord;	// UV 


uniform mat4 u_mvpMatrix;	// MODEL -> NDC
uniform mat4 u_modelMatrix;	// MODEL -> WORLD
uniform mat4 u_normalMatrix;	// MODEL -> WORLD (without scaling)

uniform vec4 u_camPosition; // Camera Position (world)

out vec4 v_normal;	// WORLD
out vec4 v_position;	// WORLD
out vec2 v_texcoord;	// UV 

void main() {
	v_texcoord = a_texcoord;
	v_normal = u_normalMatrix * a_normal;
	v_position = u_modelMatrix * a_position;
    gl_Position = u_mvpMatrix * a_position;
}