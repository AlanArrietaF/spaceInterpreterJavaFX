package fes.aragon.tablerointerprete.modelo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import fes.aragon.tablerointerprete.comando.Comando;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color; // Importamos Color para pintar a los aliens
import javafx.stage.Stage;

public class Fondo extends ComponentesJuego {

	// --- NUEVA CLASE INTERNA: ALIEN ---
	class Alien {
		int col, row;
		boolean recolectado;

		public Alien(int col, int row) {
			this.col = col;
			this.row = row;
			this.recolectado = false;
		}
	}

	private ArrayList<Alien> aliens = new ArrayList<>();
	private Random random = new Random();

	// Variables del juego
	private int yy = 0;
	private int xx = 0;
	private Image arribaImg;
	private Image abajoImg;
	private Image derechaImg;
	private Image izquierdaImg;
	private Image imagen;
	private Stage ventana;

	private ArrayList<Comando> comandos = new ArrayList<>();

	private int ancho = 40;
	private int alto = 40;
	private boolean iniciar = false;
	private GraphicsContext graficos;
	private int indice = 0;
	private int moverCuadros = 0;
	private String comando = "";

	private boolean arriba = false;
	private boolean abajo = false;
	private boolean derecha = false;
	private boolean izquierda = false;

	private int tempXx = 0;
	private boolean mostrarNave = false;

	public Fondo(int x, int y, InputStream imagen, int velocidad, Stage ventana) {
		super(x, y, imagen, velocidad);
		this.derechaImg = new Image(imagen);
		InputStream stream = getClass().getResourceAsStream("/fes/aragon/tablerointerprete/izquierda.png");
		this.izquierdaImg = new Image(stream);
		stream = getClass().getResourceAsStream("/fes/aragon/tablerointerprete/arriba.png");
		this.arribaImg = new Image(stream);
		stream = getClass().getResourceAsStream("/fes/aragon/tablerointerprete/abajo.png");
		this.abajoImg = new Image(stream);
		this.imagen = derechaImg;
		this.ventana = ventana;

		// Generamos 5 aliens al azar al abrir el programa
		generarAliens(5);
	}

	private void generarAliens(int cantidad) {
		aliens.clear();
		for(int i = 0; i < cantidad; i++) {
			int c = random.nextInt(10); // Columna al azar (0-9)
			int r = random.nextInt(10); // Fila al azar (0-9)
			aliens.add(new Alien(c, r));
		}
	}

	public void setComandos(ArrayList<Comando> nuevosComandos) {
		this.comandos = nuevosComandos;
		this.iniciar();
		this.iniciar = true;
		this.ejecutar();
	}

	@Override
	public void pintar(GraphicsContext graficos) {
		this.graficos = graficos;

		graficos.clearRect(0, 0, 600, 600);

		int coordX = 50;
		int coordY = 50;

		// 1. Dibujar Cuadrícula
		graficos.setStroke(Color.BLACK);
		for (int j = 1; j <= 10; j++) {
			for (int i = 1; i <= 10; i++) {
				graficos.strokeRect(coordX, coordY, 50, 50);
				coordX += 50;
			}
			coordX = 50;
			coordY += 50;
		}

		// 2. DIBUJAR LOS ALIENS
		graficos.setFill(Color.LIMEGREEN); // Color verde para los aliens
		for(Alien a : aliens) {
			if(!a.recolectado) {
				// Calculamos su posición en píxeles (centrado en la casilla de 50x50)
				int alienX = 60 + (a.col * 50);
				int alienY = 60 + (a.row * 50);

				// Dibujamos un círculo de 30x30.
				// (Si luego quieres usar una imagen, usa graficos.drawImage(tuImagenAlien, alienX, alienY, 30, 30);)
				graficos.fillOval(alienX, alienY, 30, 30);
			}
		}
		graficos.setFill(Color.BLACK); // Regresamos el color a negro para el texto

		// 3. Dibujar la nave
		if (mostrarNave) {
			graficos.drawImage(imagen, x, y, ancho, alto);
			graficos.strokeRect(x, y, ancho, alto);
		}

		// 4. Dibujar el texto
		if (!comandos.isEmpty()) {
			if (indice < comandos.size()) {
				graficos.strokeText("Ejecutando: " + comandos.get(indice).getAccion(), 100, 40);
			} else {
				graficos.strokeText("¡Recorrido Finalizado!", 100, 40);
			}
		}
	}

	@Override
	public void logicaCalculos() {
		if (iniciar) {
			switch (this.comando) {
				case "arriba":
				case "abajo":
				case "izquierda":
				case "derecha":
					indice++;
					this.ejecutar();
					break;

				case "mover":
					// LÓGICA PAC-MAN
					if (arriba) {
						if (y > yy) {
							y -= velocidad;
							if (y < 55) { y += 500; yy += 500; }
						} else {
							indice++; this.ejecutar();
						}
					}
					if (abajo) {
						if (y < yy) {
							y += velocidad;
							if (y > 505) { y -= 500; yy -= 500; }
						} else {
							indice++; this.ejecutar();
						}
					}
					if (izquierda) {
						if (x > xx) {
							x -= velocidad;
							if (x < 55) { x += 500; xx += 500; }
						} else {
							indice++; this.ejecutar();
						}
					}
					if (derecha) {
						if (x < xx) {
							x += velocidad;
							if (x > 505) { x -= 500; xx -= 500; }
						} else {
							indice++; this.ejecutar();
						}
					}
			}

			// VERIFICADOR DE COLISIONES: Revisa si la nave "pisó" a un alien
			verificarRecoleccion();
		}
	}

	private void verificarRecoleccion() {
		for(Alien a : aliens) {
			if(!a.recolectado) {
				int alienX = 55 + (a.col * 50);
				int alienY = 55 + (a.row * 50);

				// Si la distancia entre la nave y el alien es muy pequeña, lo recolecta
				if(Math.abs(x - alienX) < 20 && Math.abs(y - alienY) < 20) {
					a.recolectado = true;
				}
			}
		}
	}

	private void ejecutar() {
		if (indice < comandos.size()) {
			Comando c = comandos.get(indice);

			switch (c.getAccion()) {
				case "arriba":
					this.arriba = true; this.abajo = false; this.izquierda = false; this.derecha = false;
					this.imagen = this.arribaImg;
					this.comando = "arriba";
					this.mostrarNave = true;
					break;
				case "abajo":
					this.arriba = false; this.abajo = true; this.izquierda = false; this.derecha = false;
					this.imagen = this.abajoImg;
					this.comando = "abajo";
					this.mostrarNave = true;
					break;
				case "izquierda":
					this.arriba = false; this.abajo = false; this.izquierda = true; this.derecha = false;
					this.imagen = this.izquierdaImg;
					this.comando = "izquierda";
					this.mostrarNave = true;
					break;
				case "derecha":
					this.arriba = false; this.abajo = false; this.izquierda = false; this.derecha = true;
					this.imagen = this.derechaImg;
					this.comando = "derecha";
					this.mostrarNave = true;
					break;

				case "inicioX":
					this.tempXx = c.getValor();
					this.indice++;
					this.ejecutar();
					break;

				case "inicioY":
					int inicioRealX = tempXx % 10;
					int inicioRealY = c.getValor() % 10;

					xx = 55 + (ancho + 10) * inicioRealX;
					yy = 55 + (alto + 10) * inicioRealY;

					x = xx;
					y = yy;
					this.mostrarNave = true;

					this.indice++;
					this.ejecutar();
					break;

				case "mover":
					moverCuadros = c.getValor();
					if (arriba) { yy = (y - (alto + 10) * moverCuadros); }
					if (abajo) { yy = (y + (alto + 10) * moverCuadros); }
					if (izquierda) { xx = (x - (ancho + 10) * moverCuadros); }
					if (derecha) { xx = (x + (ancho + 10) * moverCuadros); }
					this.comando = "mover";
					this.mostrarNave = true;
					break;

				case "repite":
				case "parar":
					this.indice++;
					this.ejecutar();
					break;
			}

		} else {
			this.iniciar = false;
			this.indice = 0;
		}
	}

	private void iniciar() {
		this.mostrarNave = false;
		x = 55;
		y = 55;
		xx = 55;
		yy = 55;
		indice = 0;
		this.imagen = this.derechaImg;
		moverCuadros = 0;
		comando = "";
		arriba = false; abajo = false; derecha = true; izquierda = false;

		// Si el usuario vuelve a presionar "Ejecutar", revivimos a los aliens
		// para que pueda volver a intentar la misma ruta
		for(Alien a : aliens) {
			a.recolectado = false;
		}
	}

	@Override
	public void teclado(KeyEvent evento, boolean presiona) {}

	@Override
	public void raton(KeyEvent evento) {}
}