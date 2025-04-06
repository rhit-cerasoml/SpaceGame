#version 450 core

//use the outColor variable from the vertex shader as the input to the fragment shader
in vec3 outColor;


uniform sampler2D textureSampler;


layout(location = 0) out vec4 fragColor;

void main() {
    // Pass through our original color with full opacity.
    // fragColor = vec4(outColor.xyz, 1.0);
    fragColor = texture(textureSampler, outColor.xy);
}