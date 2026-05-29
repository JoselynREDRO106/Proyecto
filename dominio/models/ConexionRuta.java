package dominio.models;

public class ConexionRuta {
    private int origenId;
    private int destinoId;
    private int distanciaMetros;
    private boolean bidireccional;

    public int getOrigenId() {
        return origenId;
    }

    public void setOrigenId(int origenId) {
        this.origenId = origenId;
    }

    public int getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(int destinoId) {
        this.destinoId = destinoId;
    }

    public int getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(int distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public boolean isBidireccional() {
        return bidireccional;
    }

    public void setBidireccional(boolean bidireccional) {
        this.bidireccional = bidireccional;
    }
}
