#version 410

uniform vec3 u_intensity;        // source intensity (RGB)
uniform vec3 u_ambientIntensity; // ambient intensity (RGB)
uniform vec4 u_camPosition; // cam Position (WORLD)

uniform float u_time; //system time

uniform vec4 u_lightDirection; // light direction (used for directional light, and facing for point lights)
uniform vec4 u_lightPosition;  // light position (passed as all 0s if the light is directional)  

uniform vec3 u_diffuseMaterial;  // diffuse material cofficients (RGB)
uniform vec3 u_specularMaterial;  // specular material cofficients (RGB)
uniform float u_specularity; 	// specularity constant

uniform vec4 u_waterColour; //colour of the water

uniform int u_mode; //0 is standard, 1 is normals, 2 is UVs

in vec2 v_texcoord;	// UV (used for debug only)
in vec4 v_normal;   // interpolated surface normal (WORLD)
in vec4 v_position; // interpolated fragment position (WORLD)

const float GAMMA = 2.2;
const float WATER_MAX_ALPHA = 1f; // controls transparency of water with fresnel effect
const float WATER_MIN_ALPHA = 0.2f; // controls transparency of water with fresnel effect
const float LIGHT_ANGLE = 0.3f;
const float SCROLL_SPEED = 3f;
const float FREQUENCY = 10f;
const float AMPLITUDE = 0.4f;

layout(location = 0) out vec4 o_colour;

void main() {
	float x = (v_position.x * FREQUENCY + (u_time * SCROLL_SPEED)) * AMPLITUDE;
	vec4 normal = v_normal + vec4(-cos(x), 1, 0, 0);
    normal = normalize(normal);
    	
	vec4 output = vec4(0);
	if (u_mode == 1){ //Normals debug mode
		output = vec4(normal.x, normal.y, normal.z, 1);
	}
	else if (u_mode == 2){ //UVs debug mode
		float u = mod(v_texcoord.x, 1);
		float v = mod(v_texcoord.y, 1);
	
    	output = vec4(u, v, 0, 1);
	}
	else {
	    vec4 s = vec4(0);
	    vec4 r = vec4(0);
	    vec4 v = normalize(u_camPosition - v_position); //Calculate vector from fragment to camera
	  	
	  	vec3 intensity = u_intensity;
	  	
	  	vec3 material = u_waterColour.xyz;
	  	vec3 specular = vec3(0);
	    vec3 specularMaterial = u_specularMaterial;
		material = pow(material, vec3(GAMMA)); // Gamma correction B > I
	
	    vec3 ambient = u_ambientIntensity * material; //Alter the ambient intensity to match the material.
	    
	    
	    if (u_lightPosition.w == 0){ //If position is passed as a vector, the light is directional
	    	//Daytime Mode
	    	s = normalize(u_lightDirection); //Treat it as directional and just take the direction
	    	intensity = u_ambientIntensity;
	    	if (dot(s,normal) > 0) { //If the angle between light and fragment is positive 
		    	r = normalize(-reflect(s, normal));
				specular = u_intensity * specularMaterial * pow(max(0,dot(r,v)), u_specularity); //calculates specularity
			}
	    } 
	    else {
	    	//Nighttime mode
	    	s = normalize(u_lightPosition - v_position); //Otherwise calculate it's relative vector direction.
	    	if (dot(normalize(u_lightDirection), s) < LIGHT_ANGLE){ //If the surface is located away from the light source enough, receive no light 
	    		intensity = u_ambientIntensity;
	    		
	    		
	    	} else {
	    		if (dot(s,normal) > 0) { //If the angle between light and fragment is positive AND the fragment is within the light angle
		   			r = normalize(-reflect(s, normal));
		   			//phone o'clock
					specular = u_intensity * specularMaterial * pow(max(0,dot(r,v)), u_specularity); //calculates specularity
				}
	    	}
	    }

	    vec3 diffuse = intensity * material * max(0, dot(s,normal)); //Lamberts diffuse
	    
		vec3 finalIntensity = ambient + diffuse + specular;
		vec3 brightness = pow(finalIntensity, vec3(1./GAMMA)); // gamma coorection I > B
		
	
		float t = max(0, 1 - dot(v,normal)); //calcualtes fresnel value t
		t = (t * (WATER_MAX_ALPHA - WATER_MIN_ALPHA)) + WATER_MIN_ALPHA; //scales t between min and max values
		//The fresnel does appear to have some effect, but it is not clear what it is meant to look like - this is where example videos would have been invaluable.
	    output = vec4(brightness, t);
	}
    o_colour = output;
}