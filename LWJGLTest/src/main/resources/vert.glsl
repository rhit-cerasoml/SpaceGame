#version 450 core

//position is bound to attribute index 0 and color is bound to attribute index 1
layout (location = 0) in vec2 position;
layout (location = 1) in vec3 color;

//output the outColor variable to the next shader in the chain
out vec3 outColor;

void main() {

    //syntax: vec4(x, y, z, w);
    gl_Position = vec4(position.x, position.y, 0.0, 1.0);

    //pass the output color right to the fragment shader without changing it
    outColor = color;
}