package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Fondo {
    double x, y, escala;
    Image imagenFondo;
    Entorno e;
    double anchoImagen;
    
    public Fondo(double x, double y, double escala, Entorno e) {
        this.x = x;
        this.y = y;
        this.escala = escala;
        this.imagenFondo = Herramientas.cargarImagen("juego/fondonuevo.png");
        this.e = e;
        
        if (this.imagenFondo != null) {
            this.anchoImagen = this.imagenFondo.getWidth(null) * this.escala;
        } else {
            this.anchoImagen = 800;
        }
    }
    
    public void dibujar() {
        if (imagenFondo != null) {
            e.dibujarImagen(imagenFondo, this.x, this.y, 0, this.escala);
            
            if (this.x + this.anchoImagen/2 < e.ancho()) {
                e.dibujarImagen(imagenFondo, this.x + this.anchoImagen, this.y, 0, this.escala);
            }
            
            if (this.x - this.anchoImagen/2 > 0) {
                e.dibujarImagen(imagenFondo, this.x - this.anchoImagen, this.y, 0, this.escala);
            }
        } else {
            e.dibujarRectangulo(e.ancho()/2, e.alto()/2, e.ancho(), e.alto(), 0, new java.awt.Color(135, 206, 235));
        }
    }
}