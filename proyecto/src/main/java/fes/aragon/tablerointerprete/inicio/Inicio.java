package fes.aragon.tablerointerprete.inicio;

import fes.aragon.tablerointerprete.extras.MusicaCiclica;
import fes.aragon.tablerointerprete.modelo.Fondo;

import fes.aragon.tablerointerprete.analizador.Lexer;
import fes.aragon.tablerointerprete.analizador.parser;
import fes.aragon.tablerointerprete.comando.Comando;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class Inicio extends Application {
    private GraphicsContext graficos;
    private BorderPane root; // Cambiamos de Group a BorderPane
    private Scene escena;
    private Canvas hoja;
    private Thread hiloFondo;
    private Fondo fondo;
    private Stage ventana;

    // Nuevos componentes de la Interfaz Gráfica
    private TextArea areaInstrucciones;
    private TextArea areaErrores;
    private Button btnEjecutar;

    @Override
    public void start(Stage ventana) {
        this.ventana = ventana;
        componentesIniciar();
        pintar();
        eventosTeclado();
        this.ventana.setScene(escena);
        this.ventana.setTitle("Intérprete de Movimientos");
        this.ventana.show();
        ciclo();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stop() throws Exception {
        if(hiloFondo != null && hiloFondo.isAlive()) {
            hiloFondo.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void componentesIniciar() {
        // 1. Inicializamos el contenedor principal
        root = new BorderPane();

        // Hacemos la ventana más ancha para acomodar el panel derecho (900x700)
        escena = new Scene(root, 900, 700);

        // 2. EL CENTRO (Tu Canvas del juego)
        hoja = new Canvas(600, 600);
        root.setCenter(hoja); // Colocamos el canvas en el centro
        graficos = hoja.getGraphicsContext2D();

        // 3. LA DERECHA (Panel de instrucciones)
        areaInstrucciones = new TextArea();
        areaInstrucciones.setPromptText("Escribe tus instrucciones aquí...\nEjemplo:\ninicio 0 3\nmover 2\nderecha");
        areaInstrucciones.setPrefWidth(250);

        btnEjecutar = new Button("Ejecutar Código");
        btnEjecutar.setMaxWidth(Double.MAX_VALUE); // Para que ocupe todo el ancho

        VBox panelDerecho = new VBox(10); // 10px de separación
        panelDerecho.setPadding(new Insets(10));
        panelDerecho.getChildren().addAll(areaInstrucciones, btnEjecutar);
        root.setRight(panelDerecho);

        // 4. ABAJO (Consola de errores/salida)
        areaErrores = new TextArea();
        areaErrores.setEditable(false);
        areaErrores.setPromptText("Consola de salida y errores...");
        areaErrores.setStyle("-fx-text-fill: red; -fx-font-family: monospace; -fx-font-size: 14px;");
        areaErrores.setPrefHeight(100);
        root.setBottom(areaErrores);

        // Inicializar Música y Fondo
        MusicaCiclica entrada = new MusicaCiclica("musica_entrada");
        hiloFondo = new Thread(entrada);
        // hiloFondo.start();
        InputStream stream = getClass().getResourceAsStream("/fes/aragon/tablerointerprete/derecha.png");
        fondo = new Fondo(55, 55, stream, 1, ventana);

        // Asignar el evento al botón
        eventosBoton();
    }

    private void eventosBoton() {
        btnEjecutar.setOnAction(evento -> {
            areaErrores.clear();
            String codigo = areaInstrucciones.getText();

            if(codigo.trim().isEmpty()){
                areaErrores.setStyle("-fx-text-fill: red;");
                areaErrores.setText("Por favor, escribe algunas instrucciones antes de ejecutar.");
                return;
            }

            try {
                // Instanciamos el compilador
                Lexer lexer = new Lexer(new StringReader(codigo));
                parser p = new parser(lexer);
                p.parse();

                // Extraemos la lista
                ArrayList<Comando> comandosListos = p.listaComandos;

                // 1. Quitamos los colores y ponemos un mensaje neutral de inicio
                areaErrores.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
                areaErrores.setText("Despegando... Analizando ruta.");

                // 2. Le pasamos la consola al Fondo para que nos avise si choca o gana
                fondo.setConsolaUI(areaErrores);
                fondo.setComandos(comandosListos);

            } catch (Exception e) {
                // 3. Si hay error sintáctico, lo decimos amablemente sin color rojo
                areaErrores.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
                areaErrores.setText("Revisa tu código, parece que hay un pequeño error:\n" + e.getMessage());
            }
        });
    }

    public void ciclo() {
        long tiempoInicio = System.nanoTime();
        AnimationTimer tiempo = new AnimationTimer() {
            @Override
            public void handle(long tiempoActual) {
                double t = (tiempoActual - tiempoInicio) / 1000000000.0;
                calculosLogica();
                pintar();
            }
        };
        tiempo.start();
    }

    private void pintar() {
        this.fondo.pintar(graficos);
    }

    private void calculosLogica() {
        this.fondo.logicaCalculos();
    }

    private void eventosTeclado() {
        escena.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent arg0) {
                // Este método lo mantengo por ahora para que tu programa no crashee,
                // pero dejaremos de usar el teclado una vez que ejecutemos comandos.
                fondo.teclado(arg0, true);
            }
        });
    }
}