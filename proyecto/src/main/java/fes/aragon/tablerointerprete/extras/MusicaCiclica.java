/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fes.aragon.tablerointerprete.extras;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author Administrador
 */
public class MusicaCiclica implements Runnable {

	private BufferedInputStream buffer = null;
	private Player player = null;
	private FileInputStream archivo;
	private String nombreArchivo;

	public MusicaCiclica(String archivo) {
		this.nombreArchivo = archivo;
		try {
			this.archivo = new FileInputStream(
					this.getClass().getResource("/fes/aragon/tablerointerprete/" + nombreArchivo + ".mp3").toURI().getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			buffer = new BufferedInputStream(archivo);
			player = new Player(buffer);
			player.play();
			while (true) {
				if (player.isComplete()) {
					archivo.close();
					try {
						this.archivo = new FileInputStream(
								this.getClass().getResource("/fes/aragon/videojuego/" + nombreArchivo + ".mp3").toURI().getPath());
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					buffer = new BufferedInputStream(archivo);

					player = new Player(buffer);
					player.play();

				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
