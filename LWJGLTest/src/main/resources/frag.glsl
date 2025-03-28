#version 450 core

//use the outColor variable from the vertex shader as the input to the fragment shader
in vec3 outColor;

//the built in pixel color variable
out vec4 gl_FragColor;

void main(void) {
    // Pass through our original color with full opacity.
    gl_FragColor = vec4(outColor.r, outColor.g, outColor.b, 1.0);
}