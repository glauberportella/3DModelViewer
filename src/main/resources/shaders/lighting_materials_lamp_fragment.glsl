#version 330 core

uniform vec3 lamp_Color;

out vec4 FragColor;

void main(void) {
	FragColor = vec4(lamp_Color, 1.0);
}