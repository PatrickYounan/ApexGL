#version 400 core
layout (location = 0) in vec3 aPosition;

out vec3 color;

void main() {
    gl_Position = vec4(aPosition, 1.0);
    color = vec3(0.2, 1.0, 0.6);
}