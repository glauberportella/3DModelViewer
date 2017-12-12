#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec3 Normal;
out vec3 FragPos;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
    Normal = aNormal;
    FragPos = vec3(modelMatrix * vec4(aPos, 1.0));
}