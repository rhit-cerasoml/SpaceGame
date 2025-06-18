#version 450 core

//use the outColor variable from the vertex shader as the input to the fragment shader
in vec2 outColor;


uniform sampler2D textureSampler;


layout(location = 0) out vec4 fragColor;

void main() {
    // Pass through our original color with full opacity.
    // fragColor = vec4(outColor.xyz, 1.0);
    vec4 t = texture(textureSampler, outColor.xy);
    fragColor = t;
}