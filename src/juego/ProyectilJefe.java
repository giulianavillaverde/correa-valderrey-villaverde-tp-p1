package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class ProyectilJefe {
    double x, y, rotacion;
    double velocidadX, velocidadY;
    double ancho, alto;
    boolean activo;
    int anchoPantalla, altoPantalla;
    double arriba, abajo, izquierda, derecha;
    Image imagen;
    double escala = 0.1;
    
    public ProyectilJefe(double xInicial, double yInicial, double targetX, double targetY, int anchoPantalla, int altoPantalla) {
        this.x = xInicial;
        this.y = yInicial;
        this.activo = true;
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        
        this.imagen = Herramientas.cargarImagen("juego/proyectilJefe.png");
        
        double dx = targetX - xInicial;
        double dy = targetY - yInicial;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        
        
        if (distancia > 0) {
            this.velocidadX = (dx / distancia) * 6;
            this.velocidadY = (dy / distancia) * 6;
        } else {
            this.velocidadX = 0;
            this.velocidadY = 0;
        }
        
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * this.escala;
            this.alto = this.imagen.getHeight(null) * this.escala;
        } else {
            this.ancho = 15;
            this.alto = 15;
        }
        this.rotacion = Math.atan2(dy, dx);
        actualizarColisiones();
    }
    
    public void mover() {
        x += velocidadX;
        y += velocidadY;
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (this.activo && this.imagen != null) {
            e.dibujarImagen(this.imagen, this.x, this.y, this.rotacion, this.escala);
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
}