package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Castillo {
    double x, y;
    double ancho, alto;
    double escala;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    
    public Castillo(double x, double y, Entorno e) {
        this.x = x;
        this.y = y;
        this.escala = 0.20;
        this.activo = true;
        
        this.imagen = Herramientas.cargarImagen("juego/castillo.png");
        
        this.ancho = this.imagen.getWidth(null) * this.escala;
        this.alto = this.imagen.getHeight(null) * this.escala;
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (!activo) return;
        e.dibujarImagen(imagen, this.x, this.y, 0, this.escala);
    }
    
    public void actualizarColisiones() {
        this.arriba = this.y - this.alto / 2;
        this.abajo = this.y + this.alto / 2;
        this.izquierda = this.x - this.ancho / 2;
        this.derecha = this.x + this.ancho / 2;
    }
}