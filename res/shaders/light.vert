#version 330

in vec3 inPosition;

uniform mat4 uMvp;

void main() {
    gl_Position = uMvp * vec4(inPosition, 1.0);
    gl_PointSize = 18.0;
}
