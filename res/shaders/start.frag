#version 330
in vec3 vPos;      // Pozice z vertex shaderu
in vec3 vNormal;   // Normála (vypočtená)
in vec2 vTexCoord; // Souřadnice do textury (u, v)

uniform int debugMode; // 0: Barva, 1: Pozice, 2: Normály, 3: Textury...

out vec4 outColor;

void main() {
    if (debugMode == 0) {
        // Základní barva podle výšky (zůstává co jsi měla)
        outColor = vec4(0.0, vPos.z + 0.5, 1.0, 1.0);
    }
    else if (debugMode == 1) {
        // Vizualizace pozice XYZ jako barev RGB
        // Musíme to trochu upravit do rozsahu 0-1
        outColor = vec4(vPos * 0.5 + 0.5, 1.0);
    }
    else if (debugMode == 2) {
        // Vizualizace normál (směrů ploch)
        outColor = vec4(vNormal * 0.5 + 0.5, 1.0);
    }
    else if (debugMode == 3) {
        // Vizualizace UV souřadnic (do textury)
        outColor = vec4(vTexCoord, 0.0, 1.0);
    }
    else if (debugMode == 4) {
        // Hloubka (z-souřadnice z pohledu kamery)
        float depth = gl_FragCoord.z;
        outColor = vec4(vec3(depth), 1.0);
    }
}