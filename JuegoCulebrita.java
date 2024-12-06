import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class JuegoCulebrita extends JPanel implements ActionListener, KeyListener {
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

    public JuegoCulebrita() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setFocusable(true);
        addKeyListener(this);

        inicializarJuego();

        // Temporizador para animar el fondo del menú
        animacionMenu = new Timer(50, e -> {
            desplazamientoGradiente = (desplazamientoGradiente + 5) % getHeight();
            repaint();
        });
        animacionMenu.start();
    }

    private void inicializarJuego() {
        culebra.clear();
        culebra.add(new Point(5, 5));
        culebra.add(new Point(4, 5));
        culebra.add(new Point(3, 5));

        generarComida();
        puntuacion = 0;
        timer = new Timer(150, this);
    }

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

    private void mostrarMenuInicio(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Fondo animado
        GradientPaint gradiente = new GradientPaint(
                0, desplazamientoGradiente, colorFondo1,
                0, desplazamientoGradiente + getHeight(), colorFondo2);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Texto del título
        g.setFont(fuenteGrande);
        g.setColor(Color.YELLOW);
        String titulo = "Juego de la Culebrita";
        FontMetrics metrics = getFontMetrics(g.getFont());
        int xTitulo = (ANCHO - metrics.stringWidth(titulo)) / 2;
        int yTitulo = ALTO / 3;
        g.drawString(titulo, xTitulo, yTitulo);

        // Sombra del título
        g.setColor(Color.ORANGE);
        g.drawString(titulo, xTitulo - 5, yTitulo - 5);

        // Instrucciones    
        g.setFont(fuente);
        String instruccion = "Presiona ENTER para empezar";
        int xInstruccion = (ANCHO - metrics.stringWidth(instruccion)) / 2;
        g.setColor(Color.WHITE);
        g.drawString(instruccion, xInstruccion, yTitulo + 80);
        

        String mensaje = "Controles: Usa las flechas para moverte";
        FontMetrics metricsMensaje = g.getFontMetrics(g.getFont());
        int xMensaje = (getWidth() - metricsMensaje.stringWidth(mensaje)) / 2; // Usa getWidth() en lugar de ANCHO
        int yMensaje = yTitulo + 120; // Posición vertical relativa al título
        g.drawString(mensaje, xMensaje, yMensaje);
        
    }

    private void dibujarJuego(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradiente = new GradientPaint(0, 0, Color.DARK_GRAY, 0, getHeight(), Color.BLACK);
        g2d.setPaint(gradiente);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar marcador
        g.setColor(Color.WHITE);
        g.setFont(fuente);
        g.drawString("Puntuación: " + puntuacion, 10, 20);

        // Dibujar comida
        g.setColor(Color.RED);
        g.fillOval(comida.x * TAMANO_CELDA, comida.y * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
        g.setColor(Color.YELLOW);
        g.drawOval(comida.x * TAMANO_CELDA, comida.y * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);

        // Dibujar culebra
        for (int i = 0; i < culebra.size(); i++) {
            Point segmento = culebra.get(i);
            if (i == 0) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(new Color(0, 150, 0));
            }
            g.fillRect(segmento.x * TAMANO_CELDA, segmento.y * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
            g.setColor(Color.BLACK);
            g.drawRect(segmento.x * TAMANO_CELDA, segmento.y * TAMANO_CELDA, TAMANO_CELDA, TAMANO_CELDA);
        }
    }

    private void mostrarMensajeFin(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(fuenteGrande);
        String mensaje = "Juego Terminado";
        FontMetrics metrics = getFontMetrics(g.getFont());
        int x = (ANCHO - metrics.stringWidth(mensaje)) / 2;
        int y = ALTO / 2 - 50;
        g.drawString(mensaje, x, y);

        g.setFont(fuente);
        String reinicio = "Presiona R para reiniciar";
        int xReinicio = (ANCHO - metrics.stringWidth(reinicio)) / 2;
        g.drawString(reinicio, xReinicio, y + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (enJuego) {
            moverCulebra();
            verificarColisiones();
            repaint();
        }
    }

    private void moverCulebra() {
        Point cabeza = culebra.get(0);
        Point nuevaCabeza = new Point(cabeza);

        switch (direccion) {
            case 'U' -> nuevaCabeza.y -= 1;
            case 'D' -> nuevaCabeza.y += 1;
            case 'L' -> nuevaCabeza.x -= 1;
            case 'R' -> nuevaCabeza.x += 1;
        }

        culebra.add(0, nuevaCabeza);

        if (!nuevaCabeza.equals(comida)) {
            culebra.remove(culebra.size() - 1);
        } else {
            generarComida();
            puntuacion += 10;
        }
    }

    private void verificarColisiones() {
        Point cabeza = culebra.get(0);

        if (cabeza.x < 0 || cabeza.x >= ANCHO / TAMANO_CELDA || cabeza.y < 0 || cabeza.y >= ALTO / TAMANO_CELDA) {
            enJuego = false;
            timer.stop();
        }

        for (int i = 1; i < culebra.size(); i++) {
            if (cabeza.equals(culebra.get(i))) {
                enJuego = false;
                timer.stop();
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (enMenu && key == KeyEvent.VK_ENTER) {
            enMenu = false;
            enJuego = true;
            inicializarJuego();
            timer.start();
        } else if (enJuego) {
            if (key == KeyEvent.VK_UP && direccion != 'D') direccion = 'U';
            if (key == KeyEvent.VK_DOWN && direccion != 'U') direccion = 'D';
            if (key == KeyEvent.VK_LEFT && direccion != 'R') direccion = 'L';
            if (key == KeyEvent.VK_RIGHT && direccion != 'L') direccion = 'R';
        } else if (key == KeyEvent.VK_R) {
            enMenu = true;
            enJuego = false;
            inicializarJuego();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Culebrita con Menú Llamativo");
        JuegoCulebrita juego = new JuegoCulebrita();
        ventana.add(juego);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}