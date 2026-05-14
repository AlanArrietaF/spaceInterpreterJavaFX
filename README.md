#  Space Interpreter (Intérprete de Movimientos en Java)

Un videojuego educativo de recolección en un entorno de mapa toroidal (efecto Pac-Man) impulsado por un **Intérprete de Lenguaje Propio**. El núcleo del proyecto consiste en un analizador léxico y sintáctico que procesa scripts de texto creados por el usuario para controlar los movimientos de una nave espacial en una cuadrícula.

## Tecnologías Utilizadas

* **Lenguaje:** Java 17+
* **Interfaz Gráfica:** JavaFX (Implementación sin módulo estricto usando `Launcher` para compatibilidad con librerías heredadas).
* **Analizador Léxico:** JFlex
* **Analizador Sintáctico:** CUP (Construction of Useful Parsers)
* **Gestor de Dependencias:** Maven

## Arquitectura Técnica Destacada

El desarrollo de este proyecto aborda varios conceptos avanzados de Ciencias de la Computación y Desarrollo de Videojuegos:

### 1. Motor de Compilación Básico (JFlex & CUP)
Se diseñó un lenguaje de dominio específico (DSL) con palabras reservadas (`inicio`, `mover`, `derecha`, `izquierda`, `arriba`, `abajo`, `repite`, `parar`). 
* **Prevención de Errores:** En lugar de fallos silenciosos, el analizador está configurado para arrojar excepciones de tiempo de ejecución (`RuntimeException`) ante errores léxicos o de sintaxis, capturándolos y mostrándolos en una consola gráfica interactiva.
* **Desenrollado de Ciclos (Loop Unrolling):** Durante la etapa de análisis sintáctico (Bottom-Up), el parser desenrolla dinámicamente los bloques `repite N ... parar`. La UI nunca se entera de que existió un ciclo; simplemente recibe una lista plana de acciones, lo que optimiza el motor de renderizado.

### 2. Action Queue (Cola de Acciones)
La arquitectura separa estrictamente el compilador del motor de juego. El parser genera una estructura `ArrayList<Comando>` (Action Queue) que luego se inyecta en el objeto gráfico (`Fondo.java`). El ciclo de actualización gráfica (`AnimationTimer` a 60 FPS) consume y ejecuta esta lista de forma secuencial y fluida.

### 3. Sistema de Coordenadas y Animación Dinámica
* **Cálculo Espacial:** Traducción de coordenadas abstractas de una matriz 10x10 (0 a 9) a coordenadas de píxeles reales en un `Canvas` de JavaFX.
* **Interpolación Visual:** Los comandos no teletransportan la nave a su destino final (excepto el comando de `inicio`). Las funciones de lógica matemática actualizan la posición píxel por píxel según un vector de `velocidad`, logrando animaciones de deslizamiento suaves entre casillas.

### 4. Mapa Toroidal y Recolección
* **Efecto Pac-Man:** Implementación de límites de pantalla dinámicos. Si el objeto cruza los límites absolutos del `Canvas`, el algoritmo aplica una corrección matemática (saltos de píxeles equivalentes al ancho/alto del tablero) para crear la ilusión óptica de un espacio continuo.
* **Detección de Colisiones (AABB Básico):** Cálculo de distancias absolutas (`Math.abs`) durante el ciclo de actualización para detectar si la nave "pisa" la hitbox de objetos recolectables (Aliens) generados aleatoriamente vía la clase `Random`.

## Cómo Ejecutar el Proyecto

1.  Clona este repositorio:
    ```bash
    git clone https://github.com/AlanArrietaF/spaceInterpreterJavaFX
    ```
2.  Abre el proyecto en tu IDE favorito.
3.  Asegúrate de tener configurado el SDK de Java 17 o superior.
4.  Recarga el proyecto de Maven para descargar las dependencias de JavaFX.
5.  Ejecuta la clase principal **`Launcher.java`**.
    *(Nota: Se utiliza un `Launcher` independiente que no extiende de `Application` para evitar los conflictos clásicos del Java Module System con la librería de CUP).*

## Ejemplo de Uso (Script válido)

```text
inicio 0 0
derecha
mover 4
repite 3
  abajo
  mover 2
  derecha
  mover 2
parar
