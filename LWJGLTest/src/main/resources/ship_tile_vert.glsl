#version 450 core

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in int tid;

layout (location = 0) uniform int test;
layout (location = 1) uniform mat3 transform;

out vec2 fragUV;

void main() {
    vec3 pos = vec3(position.x, position.y, 0.0);

    pos = transform * pos;

    pos.x = pos.x / 16 * 10;
    pos.y = pos.y / 9 * 10;

    gl_Position = vec4(pos, 1.0);
    fragUV = uv;
}