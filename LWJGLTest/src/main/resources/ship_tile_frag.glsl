#version 450 core

in vec2 fragUV;

uniform sampler2D atlas;
uniform sampler2DArray my_sampler;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 t = texture(my_sampler, vec3(fragUV.xy, 0));
    //vec4 t = texture(atlas, fragUV.xy);
    fragColor = t;
    //fragColor = vec4(fragUV, 1.0f, 1.0f);
}