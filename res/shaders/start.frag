#version 330
in vec3 vPos;      // Pozice ve světě (předaná z VS)
in vec3 vNormal;   // Normála ve světě
in vec2 vTexCoord; // UV souřadnice

uniform int debugMode;
uniform vec3 lightPos; // Pozice světla (posíláš z Renderer.java)
uniform vec3 viewPos;  // Pozice kamery (posíláš z Renderer.java)
uniform sampler2D uTexture; // Pokud máš texturu, použijeme ji

out vec4 outColor;

void main() {
    // 1. PŘÍPRAVA VEKTORŮ
    vec3 normal = normalize(vNormal);
    vec3 lightDir = normalize(lightPos - vPos);
    vec3 viewDir = normalize(viewPos - vPos);
    // Halfway vektor pro Blinn-Phong (místo vektoru odrazu)
    vec3 halfwayDir = normalize(lightDir + viewDir);

    // 2. NASTAVENÍ BAREV
    vec3 lightColor = vec3(1.0, 1.0, 1.0); // Bílé světlo
    // Barva objektu (v debugMode 0 použijeme modrou, jindy třeba texturu)
    vec3 objectColor = vec3(0.0, 0.4, 0.8);

    // 3. VÝPOČET SLOŽEK OSVĚTLENÍ (Blinn-Phong)

    // Ambientní (stálé slabé světlo)
    float ambStrength = 0.2;
    vec3 ambient = ambStrength * lightColor;

    // Difuzní (matné světlo podle úhlu)
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    // Spekulární (lesk - Blinn-Phong)
    float shininess = 32.0;
    float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);
    vec3 specular = spec * lightColor;

    // 4. LOGIKA PŘEPÍNÁNÍ SLOŽEK (Zadání bod: Jednotlivé složky samostatně zapínejte)
    vec3 finalLight;

    if (debugMode == 1) {
        finalLight = ambient; // Pouze ambientní
    }
    else if (debugMode == 2) {
        finalLight = diffuse; // Pouze difuzní
    }
    else if (debugMode == 3) {
        finalLight = specular; // Pouze odlesk
    }
    else if (debugMode == 4) {
        // Starý debug pro normály (vždycky se hodí u obhajoby)
        outColor = vec4(normal * 0.5 + 0.5, 1.0);
        return;
    }
    else {
        // Default (debugMode 0): Všechny složky dohromady
        finalLight = ambient + diffuse + specular;
    }

    // 5. FINÁLNÍ BARVA (Kombinace světla a barvy objektu)
    // Pokud chceš texturu, změň objectColor na: texture(uTexture, vTexCoord).rgb
    outColor = vec4(finalLight * objectColor, 1.0);
}