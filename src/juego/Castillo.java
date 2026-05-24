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
        this.escala = 0.5;
        this.activo = true;
        
        this.imagen = Herramientas.cargarImagen("juego/castillo.png");
        
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * this.escala;
            this.alto = this.imagen.getHeight(null) * this.escala;
        } else {
            this.ancho = 80;
            this.alto = 100;
        }
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (!activo) return;
        
        if (imagen != null) {
            e.dibujarImagen(imagen, this.x, this.y, 0, this.escala);
        } else {
            e.dibujarRectangulo(this.x, this.y + 20, this.ancho, this.alto - 30, 0, java.awt.Color.GRAY);
            e.dibujarRectangulo(this.x - this.ancho/3, this.y - 10, this.ancho/3, this.alto/2, 0, java.awt.Color.DARK_GRAY);
            e.dibujarRectangulo(this.x + this.ancho/3, this.y - 10, this.ancho/3, this.alto/2, 0, java.awt.Color.DARK_GRAY);
            e.dibujarTriangulo(this.x, this.y - 30, 50, 60, 0, java.awt.Color.RED);
            e.dibujarRectangulo(this.x, this.y + 30, 20, 30, 0, java.awt.Color.BLACK);
        }
    }
    
    public void actualizarColisiones() {
        this.arriba = this.y - this.alto / 2;
        this.abajo = this.y + this.alto / 2;
        this.izquierda = this.x - this.ancho / 2;
        this.derecha = this.x + this.ancho / 2;
    }
}