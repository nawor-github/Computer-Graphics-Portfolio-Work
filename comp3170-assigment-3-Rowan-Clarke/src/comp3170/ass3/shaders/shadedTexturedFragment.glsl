#version 410

uniform vec3 u_intensity;        // source intensity (RGB)
uniform vec3 u_ambientIntensity; // ambient intensity (RGB)
uniform vec3 u_diffuseMaterial;  // diffuse material cofficients (RGB)

uniform sampler2D u_texture;

uniform vec4 u_lightDirection; // light direction (used for directional light, and facing for point lights)
uniform vec4 u_lightPosition;  // light position (passed as all 0s if the light is directional)

uniform int u_mode; //0 is standard, 1 is normals, 2 is UVs

in vec2 v_texcoord;	// UV 
in vec4 v_normal;                // interpolated surface normal (WORLD)
in vec4 v_position;              // interpolated fragment position (WORLD)

const float GAMMA = 2.2;
const float LIGHT_ANGLE = 0.3f;

layout(location = 0) out vec4 o_colour;

void main() {
	vec4 n = normalize(v_normal);
	vec4 output = vec4(0);
	if (u_mode == 1){ //Normals debug mode
		output = vec4(n.x, n.y, n.z, 1);
	}
	else if (u_mode == 2){ //UVs debug mode
		float u = mod(v_texcoord.x, 1);
		float v = mod(v_texcoord.y, 1);
	
    	output = vec4(u, v, 0, 1);
	}
	else {
	    vec4 s = vec4(0,0,0,0);
	    if (u_lightPosition.w == 0){ //If position is passed as a vector, the light is directional
	    	s = normalize(u_lightDirection); //Treat it as directional and just take the direction
	    } 
	    else {
	    	s = normalize(u_lightPosition - v_position); //Otherwise calculate it's relative vector direction.
	    }
	
	    vec4 r = vec4(0);
	    
	    vec3 material = texture(u_texture, v_texcoord) .rgb;
		material = pow(material, vec3(GAMMA)); // Gamma correction B > I
	
	    vec3 ambient = u_ambientIntensity * material; //Alter the ambient intensity to match the material.
	    vec3 intensity = u_intensity;
	    if (dot(normalize(u_lightDirection), s) < LIGHT_ANGLE){ //If the surface is located away from the light source enough, receive no light 
	    	intensity = u_ambientIntensity;
	    } 
		vec3 diffuse = intensity * material * max(0, dot(s,n)); //Lamberts diffuse
		
		vec3 finalIntensity = ambient + diffuse;
		vec3 brightness = pow(finalIntensity, vec3(1./GAMMA)); // gamma coorection I > B
		
	    output = vec4(brightness, 1);
	}
    o_colour = output;
}