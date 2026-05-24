package juego;

import entorno.Entorno;
import java.awt.Color;

public class ProyectilEnemigo {
    double x, y;
    double velocidadX, velocidadY;
    double ancho, alto;
    boolean activo;
    int anchoPantalla, altoPantalla;
    double arriba, abajo, izquierda, derecha;
    
    public ProyectilEnemigo(double xInicial, double yInicial, double dx, double dy, int anchoPantalla, int altoPantalla) {
        this.x = xInicial;
        this.y = yInicial;
        this.velocidadX = dx * 10;
        this.velocidadY = dy * 10;
        this.activo = true;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        this.ancho = 10;
        this.alto = 10;
        
        actualizarColisiones();
    }
    
    public void mover() {
        x += velocidadX;
        y += velocidadY;
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo) {
            e.dibujarCirculo(x, y, 8, Color.YELLOW);
        }
    }
    
    public void actualizarColisiones() {
        this.arriba = y - alto/2;
        this.abajo = y + alto/2;
        this.izquierda = x - ancho/2;
        this.derecha = x + ancho/2;
    }
    
    public boolean estaFueraDePantalla() {
        return x < -50 || x > anchoPantalla + 50 || y < -50 || y > altoPantalla + 50;
    }
    
    public void desactivar() {
        this.activo = false;
    }
}