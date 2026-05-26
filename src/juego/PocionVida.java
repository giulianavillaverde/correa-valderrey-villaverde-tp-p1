package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class PocionVida {
    double x, y;
    double ancho, alto;
    double escala;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    
    public PocionVida(double x, double y, Entorno e) {
        this.x = x;
        this.y = y;
        this.escala = 0.08;
        this.activo = true;
        
        this.imagen = Herramientas.cargarImagen("juego/pocion.png");
        
        this.ancho = this.imagen.getWidth(null) * this.escala;
        this.alto = this.imagen.getHeight(null) * this.escala;
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo) {
            e.dibujarImagen(imagen, x, y, 0, this.escala);
        }
    }
    
    public void actualizarColisiones() {
        this.arriba = y - alto/2;
        this.abajo = y + alto/2;
        this.izquierda = x - ancho/2;
        this.derecha = x + ancho/2;
    }
    
    public void desactivar() {
        this.activo = false;
    }
}