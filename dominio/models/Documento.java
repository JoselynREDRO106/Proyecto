package dominio.models;

public class Documento {
    private int id;
    private String nombre;
    private String rutaArchivo;
    private String tipoArchivo;
    private int categoriaId;
    private Integer tramiteId;
    private int usuarioSubidaId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getTramiteId() {
        return tramiteId;
    }

    public void setTramiteId(Integer tramiteId) {
        this.tramiteId = tramiteId;
    }

    public int getUsuarioSubidaId() {
        return usuarioSubidaId;
    }

    public void setUsuarioSubidaId(int usuarioSubidaId) {
        this.usuarioSubidaId = usuarioSubidaId;
    }
}
