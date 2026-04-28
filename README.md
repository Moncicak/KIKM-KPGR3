# KPGR3 2026 - Task 1

OpenGL/LWJGL projekt pro 1. průběžnou úlohu (parametrické povrchy, deferred shading, SSAO, spotlight, shadow pass).

## Požadavky

- JDK 21
- LWJGL 3 (GLFW, OpenGL, STB, JOML/native bindings podle balíčku)

Poznámka: v repozitáři není Gradle/Maven build skript, takže je nutné mít LWJGL knihovny přidané v IDE.

## Spuštění (IntelliJ IDEA)

1. Otevři projekt `kpgr3-2026`.
2. Nastav Project SDK na Java 21.
3. Přidej LWJGL 3 JAR knihovny do module dependencies.
4. Přidej cestu k LWJGL natives:
   - VM options:
   - `-Djava.library.path=/cesta/k/lwjgl/natives`
5. Spusť `App` (`src/App.java`).

## Ovládání

Viz `docs/controls.md`.

## Častý problém

Pokud aplikace spadne na:

`NoClassDefFoundError: org/lwjgl/...`

znamená to, že nejsou přidané LWJGL JAR knihovny do classpath projektu.
