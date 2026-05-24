package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Corazon {
    double x, y;
    double escala;
    Image imagen;
    boolean activo;
    
    public Corazon(double x, double y, double escala) {
        this.x = x;
        this.y = y;
        this.escala = escala;
        this.activo = true;
        this.imagen = Herramientas.cargarImagen("juego/corazon.gif");
    }
    
    public void dibujar(Entorno e) {
        if (activo && imagen != null) {
            e.dibujarImagen(imagen, x, y, 0, escala);
        } else if (activo) {
            e.dibujarRectangulo(x, y, 20, 20, 0, java.awt.Color.RED);
        }
    }
    
    public void desactivar() {
        this.activo = false;
    }
}