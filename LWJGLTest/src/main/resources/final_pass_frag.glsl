#version 450 core

in vec2 uv;

uniform sampler2D textureSampler;

layout(location = 0) out vec4 fragColor;

void main() {
   fragColor = texture(textureSampler, uv);
}