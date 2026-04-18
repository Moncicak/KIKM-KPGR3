#version 330
in vec4 inPosition;

uniform mat4 uModel;

void main() {
    gl_Position = uModel * inPosition;
}
