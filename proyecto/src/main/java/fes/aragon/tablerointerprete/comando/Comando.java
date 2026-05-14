package fes.aragon.tablerointerprete.comando;

public class Comando {
    private String accion;
    private int valor;

    public Comando(String accion, int valor) {
        this.accion = accion;
        this.valor = valor;
    }

    public Comando(String accion) {
        this.accion = accion;
        this.valor = 0;
    }

    public String getAccion() { return accion; }
    public int getValor() { return valor; }
}