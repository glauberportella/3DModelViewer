#version 330 core

uniform vec3 objectColour;
uniform vec3 lightColour;

out vec4 FragColor;

void main(void) {
	FragColor = vec4(lightColour * objectColour, 1.0);
}