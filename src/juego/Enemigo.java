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
    
    double escala = 0.08;
    
    public Enemigo(double x, double y, double velocidad, int anchoPantalla, int altoPantalla) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.activo = true;
        
        this.imagen = Herramientas.cargarImagen("juego/enemigo.png");
        
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * this.escala;
            this.alto = this.imagen.getHeight(null) * this.escala;
        } else {
            this.ancho = 20;
            this.alto = 20;
        }
        
        this.limiteIzquierdo = -50;
        this.limiteDerecho = anchoPantalla + 50;
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo) {
            if (imagen != null) {
                e.dibujarImagen(imagen, x, y, 0, this.escala);
            } else {
                e.dibujarCirculo(x, y, this.ancho, java.awt.Color.RED);
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