#version 400 core

in vec2 texCoord;
in vec4 fragColor;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = fragColor * texture(texSampler, texCoord);
}