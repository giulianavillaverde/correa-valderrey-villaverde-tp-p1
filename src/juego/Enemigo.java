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
            this.ancho = this.imagen.getWidth(null) * 0.1;
            this.alto = this.imagen.getHeight(null) * 0.1;
        } else {
            this.ancho = 25;
            this.alto = 25;
        }
        
        this.limiteIzquierdo = -50;
        this.limiteDerecho = anchoPantalla + 50;
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo) {
            if (imagen != null) {
                e.dibujarImagen(imagen, x, y, 0, 0.1);
            } else {
                e.dibujarCirculo(x, y, 15, java.awt.Color.RED);
            }
        }
    }
    
    public void mover() {
        if (activo) {
            x += velocidad;
            actualizarColisiones();
        }
    }
    
    // Método para mover con colisión de islas
    public void moverConIslas(Isla[][] islas) {
        if (!activo) return;
        
        double nuevaX = x + velocidad;
        
        // Verificar colisión con islas
        boolean colision = false;
        for(Isla[] fila : islas) {
            for(Isla isla : fila) {
                if(isla != null) {
                    double tempIzquierda = nuevaX - ancho/2;
                    double tempDerecha = nuevaX + ancho/2;
                    
                    if (tempDerecha > isla.izquierda && tempIzquierda < isla.derecha &&
                        abajo > isla.arriba && arriba < isla.abajo) {
                        colision = true;
                        break;
                    }
                }
            }
            if(colision) break;
        }
        
        if (colision) {
            velocidad = -velocidad;
        } else {
            x = nuevaX;
        }
        
        actualizarColisiones();
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