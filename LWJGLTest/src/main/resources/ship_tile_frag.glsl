#version 450 core

in vec2 fragUV;

uniform sampler2DArray atlas;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 t = texture(atlas, vec3(fragUV.xy, 0));
    fragColor = t;
}