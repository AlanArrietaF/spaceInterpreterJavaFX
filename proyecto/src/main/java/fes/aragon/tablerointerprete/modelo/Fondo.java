package fes.aragon.tablerointerprete.modelo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import fes.aragon.tablerointerprete.comando.Comando;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea; // Importante para la consola
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Fondo extends ComponentesJuego {

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

	private int yy = 0;
	private int xx = 0;
	private Image arribaImg;
	private Image abajoImg;
	private Image derechaImg;
	private Image izquierdaImg;
	private Image imagen;
	private Stage ventana;

	private ArrayList<Comando> comandos = new ArrayList<>();

	// --- NUEVA VARIABLE: Referencia a la consola de la interfaz ---
	private TextArea consolaUI;

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

	// --- NUEVA VARIABLE: Bandera de choque ---
	private boolean chocado = false;

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

		generarAliens(5);
	}

	// Método para recibir la consola desde Inicio.java
	public void setConsolaUI(TextArea consola) {
		this.consolaUI = consola;
	}

	private void generarAliens(int cantidad) {
		aliens.clear();
		for(int i = 0; i < cantidad; i++) {
			int c = random.nextInt(10);
			int r = random.nextInt(10);
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

		graficos.setStroke(Color.BLACK);
		for (int j = 1; j <= 10; j++) {
			for (int i = 1; i <= 10; i++) {
				graficos.strokeRect(coordX, coordY, 50, 50);
				coordX += 50;
			}
			coordX = 50;
			coordY += 50;
		}

		graficos.setFill(Color.LIMEGREEN);
		for(Alien a : aliens) {
			if(!a.recolectado) {
				int alienX = 60 + (a.col * 50);
				int alienY = 60 + (a.row * 50);
				graficos.fillOval(alienX, alienY, 30, 30);
			}
		}
		graficos.setFill(Color.BLACK);

		if (mostrarNave) {
			graficos.drawImage(imagen, x, y, ancho, alto);
			graficos.strokeRect(x, y, ancho, alto);
		}
	}

	@Override
	public void logicaCalculos() {
		if (iniciar && !chocado) {
			switch (this.comando) {
				case "arriba":
				case "abajo":
				case "izquierda":
				case "derecha":
					indice++;
					this.ejecutar();
					break;

				case "mover":
					// LÓGICA DE MUROS (Se quitó el efecto Pac-Man)
					if (arriba) {
						if (y > yy) {
							y -= velocidad;
							if (y < 55) { y = 55; yy = 55; registrarChoque(); }
						} else { indice++; this.ejecutar(); }
					}
					if (abajo) {
						if (y < yy) {
							y += velocidad;
							if (y > 505) { y = 505; yy = 505; registrarChoque(); }
						} else { indice++; this.ejecutar(); }
					}
					if (izquierda) {
						if (x > xx) {
							x -= velocidad;
							if (x < 55) { x = 55; xx = 55; registrarChoque(); }
						} else { indice++; this.ejecutar(); }
					}
					if (derecha) {
						if (x < xx) {
							x += velocidad;
							if (x > 505) { x = 505; xx = 505; registrarChoque(); }
						} else { indice++; this.ejecutar(); }
					}
			}

			verificarRecoleccion();
		}
	}

	// --- NUEVO MÉTODO: Qué hacer cuando choca ---
	private void registrarChoque() {
		this.chocado = true;
		this.iniciar = false;
		if (consolaUI != null) {
			consolaUI.setText("Chocaste");
		}
	}

	private void verificarRecoleccion() {
		for(Alien a : aliens) {
			if(!a.recolectado) {
				int alienX = 55 + (a.col * 50);
				int alienY = 55 + (a.row * 50);

				if(Math.abs(x - alienX) < 20 && Math.abs(y - alienY) < 20) {
					a.recolectado = true;
				}
			}
		}
	}

	private void ejecutar() {
		if (indice < comandos.size() && !chocado) {
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
					// Quitamos el módulo (%) para que no sea cíclico.
					int inicioX = tempXx;
					int inicioY = c.getValor();

					// Si intenta iniciar fuera del tablero (ej. inicio 15 2), choca inmediatamente
					if(inicioX < 0 || inicioX > 9 || inicioY < 0 || inicioY > 9) {
						registrarChoque();
						return; // Detiene la ejecución
					}

					xx = 55 + (ancho + 10) * inicioX;
					yy = 55 + (alto + 10) * inicioY;

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
			// --- RUTINA DE FIN DE JUEGO ---
			this.iniciar = false;
			this.indice = 0;

			if (!chocado) {
				// Si no chocó, verificamos si agarró todos los aliens
				boolean todosRecolectados = true;
				for(Alien a : aliens) {
					if (!a.recolectado) {
						todosRecolectados = false;
					}
				}

				if (consolaUI != null) {
					if (todosRecolectados) {
						consolaUI.setText("Lograste llegar a todos los puntos");
					} else {
						consolaUI.setText("Vuelve a intentarlo");
					}
				}
			}
		}
	}

	private void iniciar() {
		this.mostrarNave = false;
		this.chocado = false; // Reiniciamos el choque

		x = 55;
		y = 55;
		xx = 55;
		yy = 55;
		indice = 0;
		this.imagen = this.derechaImg;
		moverCuadros = 0;
		comando = "";
		arriba = false; abajo = false; derecha = true; izquierda = false;

		for(Alien a : aliens) {
			a.recolectado = false;
		}

		if (consolaUI != null) {
			consolaUI.setText("Despegando... Analizando ruta.");
		}
	}

	@Override
	public void teclado(KeyEvent evento, boolean presiona) {}

	@Override
	public void raton(KeyEvent evento) {}
}