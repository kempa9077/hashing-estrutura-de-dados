public class Registro {
    private String codigo;

    public Registro(String codigo) {
        if (codigo.length() != 9) {
            throw new IllegalArgumentException("Código deve ter 9 dígitos");
        }
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registro registro = (Registro) o;
        return codigo.equals(registro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}