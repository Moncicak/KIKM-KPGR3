#version 330

in vec3 inPosition;
in vec2 inTexCoord;

uniform mat4 uMvp;
uniform mat4 uModel;
uniform float uTime;
uniform int uSurfaceType;

out vec3 vWorldPos;
out vec3 vNormal;
out vec2 vUv;

vec3 surfacePosition(vec2 uv) {
    vec2 p = uv * 2.0 - 1.0;
    float x = p.x;
    float y = p.y;
    float t = uTime;

    if (uSurfaceType == 0) {
        float r = sqrt(20.0 * x * x + 20.0 * y * y);
        return vec3(x, y, 0.35 * cos(r + 0.4 * t));
    }

    if (uSurfaceType == 1) {
        float wave = 0.22 * sin(4.0 * x + 0.5 * t) * cos(4.0 * y - 0.35 * t);
        float falloff = exp(-0.25 * (x * x + y * y));
        return vec3(x, y, wave * falloff);
    }

    float theta = uv.x * 3.14159265;
    float phi = uv.y * 6.28318530;

    if (uSurfaceType == 2) {
        float r = 1.2 + 0.35 * cos(4.0 * phi);
        return vec3(
            r * sin(theta) * cos(phi),
            r * sin(theta) * sin(phi),
            r * cos(theta)
        );
    }

    if (uSurfaceType == 3) {
        float r = 1.0 + 0.2 * sin(3.0 * theta + 0.5 * t) * cos(5.0 * phi);
        return vec3(
            r * sin(theta) * cos(phi),
            r * sin(theta) * sin(phi),
            r * cos(theta)
        );
    }

    float z = p.y;
    if (uSurfaceType == 4) {
        float r = 1.0 + 0.2 * sin(6.0 * phi + t);
        return vec3(r * cos(phi), r * sin(phi), z);
    }

    float spiralRadius = 0.82 + 0.18 * sin(8.0 * phi + 4.0 * z + 0.5 * t);
    return vec3(spiralRadius * cos(phi), spiralRadius * sin(phi), z);
}

vec3 surfaceNormal(vec2 uv) {
    float eps = 0.0025;
    vec2 uPlus = clamp(uv + vec2(eps, 0.0), vec2(0.0), vec2(1.0));
    vec2 uMinus = clamp(uv - vec2(eps, 0.0), vec2(0.0), vec2(1.0));
    vec2 vPlus = clamp(uv + vec2(0.0, eps), vec2(0.0), vec2(1.0));
    vec2 vMinus = clamp(uv - vec2(0.0, eps), vec2(0.0), vec2(1.0));
    vec3 du = surfacePosition(uPlus) - surfacePosition(uMinus);
    vec3 dv = surfacePosition(vPlus) - surfacePosition(vMinus);
    return normalize(cross(du, dv));
}

void main() {
    vec3 localPos = surfacePosition(inTexCoord);
    vec3 localNormal = surfaceNormal(inTexCoord);

    vec4 worldPos = uModel * vec4(localPos, 1.0);
    mat3 normalMatrix = transpose(inverse(mat3(uModel)));

    vWorldPos = worldPos.xyz;
    vNormal = normalize(normalMatrix * localNormal);
    vUv = inTexCoord;
    gl_Position = uMvp * vec4(localPos, 1.0);
}
