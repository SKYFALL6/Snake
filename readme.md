El juego de la culebrita se compone de una sola clase llamada `JuegoCulebrita`. Esta clase extiende `JPanel` para crear una interfaz gráfica del juego, y también implementa las interfaces `ActionListener` y `KeyListener` para manejar eventos como la acción del temporizador y las entradas del teclado.


 1. Importación de Paquetes

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
Estas son las bibliotecas que se importan para que el código funcione correctamente:

javax.swing.*: Importa las clases necesarias para crear componentes gráficos de la interfaz, como JPanel, JFrame y Timer.
java.awt.*: Importa clases para el diseño de gráficos como Graphics, Graphics2D, Color, Font, y FontMetrics.
java.awt.event.*: Importa clases para manejar eventos de teclado y temporizadores.
java.util.ArrayList: Importa la clase ArrayList que es utilizada para almacenar los segmentos de la serpiente.
java.util.Random: Importa la clase Random para generar posiciones aleatorias para la comida.


2. Declaración de Variables
java
Copiar código
private final int TAMANO_CELDA = 25;
private final int ANCHO = 800;
private final int ALTO = 600;
private final ArrayList<Point> culebra = new ArrayList<>();
private Point comida;
private char direccion = 'D';
private boolean enJuego = false;
private boolean enMenu = true;
private int puntuacion = 0;
private Timer timer, animacionMenu;
private final Font fuente = new Font("Arial", Font.BOLD, 20);
private final Font fuenteGrande = new Font("Impact", Font.BOLD, 50);
private Color colorFondo1 = Color.BLUE;
private Color colorFondo2 = Color.BLACK;
private int desplazamientoGradiente = 0;
Aquí se definen varias variables que controlan aspectos del juego:

TAMANO_CELDA: El tamaño de cada celda en la cuadrícula del juego. Esto determina cuán grande será cada segmento de la serpiente y la comida. En este caso, cada celda tiene un tamaño de 25 píxeles.
ANCHO y ALTO: Las dimensiones de la ventana del juego en píxeles. En este caso, la ventana tiene un ancho de 800 píxeles y un alto de 600 píxeles.
culebra: Una lista de puntos que representa las coordenadas de cada segmento de la serpiente. Se utiliza un ArrayList<Point> para almacenar dinámicamente las posiciones de los segmentos a medida que la serpiente crece.
comida: Un objeto Point que guarda la ubicación de la comida en el mapa.
direccion: Un carácter que indica la dirección actual de la serpiente ('U' para arriba, 'D' para abajo, 'L' para izquierda, 'R' para derecha).
enJuego: Un booleano que indica si el juego está en curso o si ha terminado.
enMenu: Un booleano que indica si el juego está mostrando el menú inicial.
puntuacion: La puntuación actual del jugador. Se incrementa cada vez que la serpiente come.
timer y animacionMenu: Objetos Timer utilizados para controlar el ritmo del juego y la animación del fondo del menú.
fuente y fuenteGrande: Fuentes tipográficas utilizadas para mostrar texto en la pantalla, como la puntuación y los mensajes.
colorFondo1 y colorFondo2: Colores utilizados para el fondo con gradiente animado en el menú.
desplazamientoGradiente: Controla el desplazamiento del fondo animado en el menú.


3. Constructor JuegoCulebrita()
java
Copiar código
public JuegoCulebrita() {
    setPreferredSize(new Dimension(ANCHO, ALTO));
    setFocusable(true);
    addKeyListener(this);

    inicializarJuego();

    animacionMenu = new Timer(50, e -> {
        desplazamientoGradiente = (desplazamientoGradiente + 5) % getHeight();
        repaint();
    });
    animacionMenu.start();
}
Este es el constructor principal de la clase JuegoCulebrita. Se encarga de:

Configuración de la ventana:

Establece el tamaño preferido del panel de juego con setPreferredSize(new Dimension(ANCHO, ALTO)).
Llama a setFocusable(true) para que el panel pueda recibir eventos de teclado.
Añade un KeyListener para manejar las entradas del teclado.
Inicialización del juego:

Llama a inicializarJuego(), que configura el estado inicial del juego.
Animación del menú:

Crea un Timer para animar el fondo del menú. Cada 50 milisegundos, se actualiza el valor de desplazamientoGradiente y se vuelve a dibujar el panel con repaint(). Esto crea un efecto de fondo animado que se mueve hacia abajo.


4. Método inicializarJuego()
java
Copiar código
private void inicializarJuego() {
    culebra.clear();
    culebra.add(new Point(5, 5));
    culebra.add(new Point(4, 5));
    culebra.add(new Point(3, 5));

    generarComida();
    puntuacion = 0;
    timer = new Timer(150, this);
}
Este método se encarga de preparar todo lo necesario para empezar una nueva partida:

Reseteo de la serpiente:

Se borra la lista de segmentos de la serpiente (culebra.clear()) y se agrega la cabeza y dos segmentos iniciales en las posiciones (5, 5), (4, 5), y (3, 5).
Generación de comida: Llama al método generarComida() para colocar la comida en una posición aleatoria en la pantalla.

Restablecimiento de la puntuación: Se pone la puntuación en cero.

Configuración del temporizador: Se configura un Timer que se activa cada 150 milisegundos y ejecuta el método actionPerformed, lo que permite que la serpiente se mueva a intervalos regulares.

5. Método generarComida()
java
Copiar código
private void generarComida() {
    Random random = new Random();
    int x = random.nextInt(ANCHO / TAMANO_CELDA);
    int y = random.nextInt(ALTO / TAMANO_CELDA);
    comida = new Point(x, y);

    while (culebra.contains(comida)) {
        x = random.nextInt(ANCHO / TAMANO_CELDA);
        y = random.nextInt(ALTO / TAMANO_CELDA);
        comida = new Point(x, y);
    }
}
Este método genera una nueva comida en una posición aleatoria que no coincida con la posición de la serpiente. Usa la clase Random para generar números aleatorios dentro de los límites del tablero, y verifica que la comida no se superponga a la serpiente con culebra.contains(comida).

6. Método paintComponent(Graphics g)
java
Copiar código
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (enMenu) {
        mostrarMenuInicio(g);
    } else if (enJuego) {
        dibujarJuego(g);
    } else {
        mostrarMensajeFin(g);
    }
}
Este método se encarga de dibujar en la pantalla. Dependiendo del estado del juego (enMenu, enJuego), se llama a diferentes métodos de dibujo:

Si el juego está en el menú (enMenu), llama a mostrarMenuInicio(g).
Si el juego está en curso (enJuego), llama a dibujarJuego(g).
Si el juego ha terminado, llama a mostrarMensajeFin(g) para mostrar el mensaje de fin de juego.


7. Método mostrarMenuInicio(Graphics g)
java
Copiar código
private void mostrarMenuInicio(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    GradientPaint gradiente = new GradientPaint(
            0, desplazamientoGradiente, colorFondo1,
            0, desplazamientoGradiente + getHeight(), colorFondo2);
    g2d.setPaint


