#version 400 core
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec2 texCoord;
out vec4 fragColor;

void main() {
    fragColor = aColor;
    texCoord = aTexCoords;
    gl_Position = uProjection * uView * vec4(aPosition, 1.0);
}
