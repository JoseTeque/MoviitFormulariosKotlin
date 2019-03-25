package m.google.moviitformularioskotlin.model;

import java.util.Date;

public class Model {

    private String nombre;
    private String email;
    private String phone;
    private String fecha;
    private String imagen;


    public Model() {
    }

    public Model(String nombre, String email, String phone,String fecha,String imagen) {
        this.nombre = nombre;
        this.email = email;
        this.phone = phone;
        this.fecha = fecha;
        this.imagen= imagen;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
