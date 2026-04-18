#version 330
in vec2 inPosition;
in vec2 inTexCoord;

out vec2 vTexCoord;

void main() {
    vTexCoord = inTexCoord;
    gl_Position = vec4(inPosition, 0.0, 1.0);
}
