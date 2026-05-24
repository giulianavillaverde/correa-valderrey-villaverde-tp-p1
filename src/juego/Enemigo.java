package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Enemigo {
    double x, y;
    double ancho, alto;
    double velocidad;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    int limiteIzquierdo;
    int limiteDerecho;
    
    public Enemigo(double x, double y, double velocidad, int anchoPantalla, int altoPantalla) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.activo = true;
        
        this.imagen = Herramientas.cargarImagen("juego/enemigo.png");
        
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * 0.15;
            this.alto = this.imagen.getHeight(null) * 0.15;
        } else {
            this.ancho = 30;
            this.alto = 30;
        }
        
        this.limiteIzquierdo = -50;
        this.limiteDerecho = anchoPantalla + 50;
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo) {
            if (imagen != null) {
                e.dibujarImagen(imagen, x, y, 0, 0.08);
            } else {
                e.dibujarCirculo(x, y, 20, java.awt.Color.RED);
            }
        }
    }
    
    public void mover() {
        if (activo) {
            x += velocidad;
            actualizarColisiones();
        }
    }
    
    public void actualizarColisiones() {
        this.arriba = y - alto/2;
        this.abajo = y + alto/2;
        this.izquierda = x - ancho/2;
        this.derecha = x + ancho/2;
    }
    
    public boolean estaFueraDePantalla() {
        return x < limiteIzquierdo || x > limiteDerecho;
    }
    
    public void desactivar() {
        this.activo = false;
    }
}