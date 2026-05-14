package fes.aragon.tablerointerprete.extras;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class EfectosMusica implements Runnable {
    private BufferedInputStream buffer = null;
    private FileInputStream archivo;
    private Player player = null;

    public EfectosMusica(String archivo) {
        try {
            this.archivo = new FileInputStream(this.getClass().getResource("/fes/aragon/tablerointerprete/" + archivo + ".mp3").toURI().getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            this.buffer = new BufferedInputStream(this.archivo);
            this.player = new Player(this.buffer);
            this.player.play();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }

    }
}