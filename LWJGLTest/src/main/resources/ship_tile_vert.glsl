#version 450 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in int tid;

out vec2 fragUV;

void main() {
    gl_Position = vec4(position.x / 16 * 10, position.y / 9 * 10, 0.0, 1.0);
    fragUV = uv;
}