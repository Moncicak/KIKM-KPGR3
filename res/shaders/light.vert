#version 330
in vec4 inPosition;

uniform mat4 modelViewProjection;

void main() {
    gl_Position = modelViewProjection * inPosition;
}
