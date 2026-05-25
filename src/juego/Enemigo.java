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
    
    // Nuevo método: mover con colisión de islas
    public void moverConIslas(Isla[][] islas) {
        if (!activo) return;
        
        double nuevaX = x + velocidad;
        
        // Verificar colisión con islas
        boolean colision = false;
        for(Isla[] fila : islas) {
            for(Isla isla : fila) {
                if (isla != null) {
                    // Calcular nueva posición temporal
                    double tempX = nuevaX;
                    double tempIzquierda = tempX - ancho/2;
                    double tempDerecha = tempX + ancho/2;
                    
                    // Verificar si hay colisión horizontal con la isla
                    if (tempDerecha > isla.izquierda && tempIzquierda < isla.derecha &&
                        abajo > isla.arriba && arriba < isla.abajo) {
                        colision = true;
                        break;
                    }
                }
            }
            if(colision) break;
        }
        
        // Si no hay colisión, mover normalmente
        if (!colision) {
            x = nuevaX;
        } else {
            // Rebotar o cambiar dirección (opcional)
            velocidad = -velocidad;
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