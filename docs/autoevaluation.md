# Autoevaluační tabulka

| Požadavek | Stav | Poznámka |
|---|---:|---|
| Kamera ovládaná myší a WSAD | splněno | First-person kamera, `Q/E` pro výšku, `R` reset |
| Přepínání ortho/perspektiva | splněno | `V` |
| Render módy body/hrany/plochy | splněno | `M` |
| Grid jako triangle list i strip | splněno | `N` |
| Parametrický povrch ve vertex shaderu | splněno částečně | 6 funkcí v shaderu, zatím bez plné celé pipeline |
| Alespoň dvě tělesa zároveň | splněno | parametrický grid + načtený OBJ model |
| Debug zobrazení atributů | splněno částečně | pozice, normály, UV, hloubka, textura, lit |
| Osvětlení bez textur | splněno | `T` přepíná texturu na konstantní barvu |
| Texturovaný osvětlený povrch | splněno | základní Blinn-Phong s přepínáním složek |
| Spotlight a útlum | splněno | spotlight, soft edge a attenuation jsou přidané |
| Řízení směru a úhlu reflektoru | splněno | `šipky` mění směr, `[` a `]` mění úhel |
| Shadow mapping | splněno částečně | depth pass + sampling v lighting pass |
| Shadow mapping | nesplněno | bude doplněno později |
| Deferred shading a ambient occlusion | nesplněno | bude doplněno později |
| Vizualizace pozice světla | splněno | světelný marker jako point |
| Verzování pomocí gitu | splněno částečně | lokální repo a více commitů, remote/private repo není ověřeno |
| Pravidelné komentované commity | splněno částečně | historie existuje, ale zatím je krátká |
| Dokumentace ovládání | splněno | tento soubor |
