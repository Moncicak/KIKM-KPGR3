#version 330
in vec4 inPosition;

uniform mat4 lightMVP;

void main() {
    gl_Position = lightMVP * inPosition;
}
