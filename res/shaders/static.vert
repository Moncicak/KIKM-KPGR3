#version 330
in vec3 inPosition;
in vec3 inNormal;   // OGLModelOBJ obvykle posílá i normály
uniform mat4 modelViewProjection;

out vec3 vPos;
out vec3 vNormal;

void main() {
    vPos = inPosition;
    vNormal = inNormal;
    gl_Position = modelViewProjection * vec4(inPosition, 1.0);
}