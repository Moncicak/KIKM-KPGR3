# Plan k 1. uloze

## Cíl
Postavit prostorovou OpenGL aplikaci nad stavajici kostrou projektu v Java + LWJGL, ktera zobrazi parametricka telesa vytvorena z rovinneho gridu trojuhelniku, s texturami, osvetlenim, stiny a vicepruchodovym zpracovanim obrazu.

## Výchozí stav projektu
- Projekt ma minimalni kostru s `App`, `LwjglWindow`, `AbstractRenderer` a jednim `Renderer`.
- K dispozici jsou utility v `src/lwjglutils` a matematicke tridy v `src/transforms`.
- Chybi build skript, dokumentace, sprava shaderu na urovni aplikace, kamera, scena i render pipeline.

## Navržená architektura

### 1. Zakladni render vrstva
- Udrzet `Renderer` jako hlavni orchestrace cele aplikace.
- Vytvorit pomocne tridy pro:
  - grid mesh generator,
  - shader program wrapper,
  - camera controller,
  - scene object / material,
  - framebuffer management pro vice pruchodu.
- Oddelit data sceny, stav ovladani a jednotlivé render passy.

### 2. Geometrie
- Generovat rovinný grid na CPU.
- Podporit dve reprezentace:
  - triangle list,
  - triangle strip.
- Umoznit prepinani rasterizace:
  - body,
  - hrany,
  - vyplnene plochy.
- Do GPU posilat jen parametry gridu, UV a pripadne indexy.
- Vlastni deformaci gridu na povrch provadet az ve vertex shaderu.

### 3. Parametricke povrchy
- Implementovat alespon 6 povrchu:
  - 2 kartézské,
  - 2 sférické,
  - 2 cylindrické.
- U kazdeho povrchu sjednotit vstupni parametry tak, aby bylo mozne povrch menit pres uniformy.
- Alespon jeden povrch animovat v case.
- Soucasne zobrazit minimalne dve telesa:
  - jedno parametricke z gridu,
  - druhe parametricke nebo nactene z modelu.

### 4. Kamera a ovladani
- Implementovat pohyb kamery:
  - `W`, `S`, `A`, `D` pro translaci,
  - mys pro rozhlizeni.
- Pridat prepinani:
  - ortho / perspektiva,
  - wireframe / fill / points,
  - debug view rezimy,
  - typ povrchu,
  - list / strip.

### 5. Material, textura a debug rezimy
- Mapovat texturu na parametricky povrch.
- Implementovat debug vystupy v fragment shaderu:
  - pozice v prostoru,
  - hloubka,
  - barva bez osvetleni,
  - textura,
  - normala,
  - UV souradnice,
  - vzdalenost od svetla.
- Vsechny debug rezimy prepinat klavesami.

### 6. Normály a osvetleni
- Vypocitat normalu ve vertex shaderu:
  - prednostne pomoci parcialnich derivaci,
  - zalozne diferenci pres male posuny parametru.
- Implementovat Blinn-Phong:
  - ambient,
  - diffuse,
  - specular.
- Jednotlive slozky musi jit samostatne zapnout a vypnout.
- Umoznit menit polohu svetla a telesa.

### 7. Reflektor a stiny
- Implementovat spotlight:
  - pozice,
  - smer,
  - utlum,
  - hladky okraj kuzele.
- Vizualizovat pozici zdroje svetla.
- Pridat shadow mapping:
  - depth pass ze svetla,
  - lighting pass se vzorkovanim shadow mapy.

### 8. Deferred shading a ambient occlusion
- Rozdelit vykreslovani na vice pruchodu.
- Prvni pruchod:
  - ulozit G-buffer, alespon pozici/depth, normalu a albedo.
- Druhy pruchod:
  - vypocet ambient occlusion.
- Treti pruchod:
  - rozmazani AO.
- Finalni pruchod:
  - slozeni osvetleni s AO.

## Milníky

### Milnik 1: Stabilni zaklad
- Uklidit `Renderer` a rozdelit zodpovednosti.
- Zavest spravu shaderu, uniform a meshi.
- Zprovoznit kameru a projekce.
- Overeni: scena se korektne hybe, kamera funguje, viewport resize funguje.

### Milnik 2: Grid a parametricka telesa
- CPU generator gridu.
- Vertex shader deformujici grid na povrch.
- Prepinani mezi vice povrchy.
- Overeni: funguje list i strip, body/hrany/plochy.

### Milnik 3: Material a debug zobrazeni
- UV, textura, debug rezimy.
- Overeni: kazdy debug mod zobrazuje smysluplna data.

### Milnik 4: Osvetleni
- Normály.
- Blinn-Phong vcetne prepinani slozek.
- Spotlight a pohyb svetla.
- Overeni: svetlo reaguje na pohyb objektu a kamery.

### Milnik 5: Stiny
- Depth framebuffer ze svetla.
- Shadow map sampling.
- Overeni: objekty vrhaji vizualne stabilni stiny.

### Milnik 6: Deferred shading + AO
- G-buffer.
- AO pass.
- Blur pass.
- Final compose.
- Overeni: AO je viditelne, ale nedegraduje zasadne vykon ani obraz.

### Milnik 7: Dokonceni odevzdani
- Dopsat dokumentaci ovladani.
- Dopsat autoevaluacni tabulku.
- Zkontrolovat strukturu projektu a finalni odevzdani.

## Doporučené pořadí implementace
1. Kamera, projekce, ovladani, cas.
2. Grid generator a prepinani list/strip.
3. Parametricke povrchy ve vertex shaderu.
4. Debug view modu a textura.
5. Normály a Blinn-Phong.
6. Spotlight.
7. Shadow mapping.
8. Deferred shading a AO.
9. Dokumentace a autoevaluace.

## Návrh povrchů

### Kartézské
- Vlna: `z = a * cos(sqrt(k * x^2 + k * y^2))`
- Vlastni navrh: kombinace sin/cos s radialnim utlumem

### Sférické
- Kvetinovy povrch: `r = 3 + cos(4 * phi)`
- Vlastni navrh: `r = 1.2 + 0.35 * sin(5 * theta) * cos(3 * phi)`

### Cylindrické
- Zvlněný valec: `r = 1.0 + 0.2 * sin(6 * phi + t)`
- Vlastni navrh: spiralove deformovany valec se zmenou polomeru podle vysky

## Rizika
- AO a shadow mapping budou nejnarocnejsi na ladeni.
- Bez build skriptu bude tezsi snadno overovat prenositelnost projektu.
- Pokud bude casovy tlak, je lepsi nejdriv dotahnout kvalitni grid + povrchy + kamera + osvetleni + stiny a AO resit az nakonec.

## Definition of done
- Projekt zobrazi nejmene dve telesa soucasne.
- Grid funguje jako list i strip.
- Existuje 6 parametrickych povrchu ve vertex shaderu.
- Kamera je ovladatelna mysi a `WSAD`.
- Jde prepinat ortho/perspektiva.
- Funguje textura, Blinn-Phong, spotlight a stiny.
- Existuje vicepruchodova pipeline s AO.
- V projektu je dokumentace ovladani a autoevaluacni tabulka.
