#version 330
in vec4 inPosition;
in vec3 inNormal;

uniform mat4 modelViewProjection;
uniform mat4 modelViewMatrix;

out vec3 vViewPos;
out vec3 vViewNormal;

void main() {
    vViewPos = vec3(modelViewMatrix * inPosition);
    vViewNormal = normalize(mat3(modelViewMatrix) * inNormal);
    gl_Position = modelViewProjection * inPosition;
}
