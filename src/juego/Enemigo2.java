package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Enemigo2 {
    double x, y, escala;
    double ancho, alto;
    double velocidad;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    int limiteIzquierdo;
    int limiteDerecho;

    // Variables para el movimiento ondulante vertical
    double yBase;
    double amplitudOndulacion;
    double frecuenciaOndulacion;
    int tickContador;

    public Enemigo2(double x, double y, double velocidad, int anchoPantalla, int altoPantalla) {
        this.x = x;
        this.y = y;
        this.yBase = y;
        this.velocidad = velocidad;
        this.activo = true;
        this.escala = 0.27;

        this.amplitudOndulacion = 18; // cuántos píxeles sube/baja
        this.frecuenciaOndulacion = 0.06; // qué tan rápido oscila
        this.tickContador = 0;

        if (this.velocidad > 0) {
            this.imagen = Herramientas.cargarImagen("juego/enemigoDer2.gif");
        } else {
            this.imagen = Herramientas.cargarImagen("juego/enemigoIzq2.gif");
        }

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
                e.dibujarCirculo(x, y, 15, java.awt.Color.MAGENTA);
            }
        }
    }

    // movimiento del enemigo
    public void mover() {
        if (activo) {
            tickContador++;
            x += velocidad;
            // Movimiento ondulante vertical leve
            y = yBase + amplitudOndulacion * Math.sin(frecuenciaOndulacion * tickContador);
            actualizarColisiones();
        }
    }

    public void actualizarColisiones() {
        this.arriba = y - alto / 2;
        this.abajo = y + alto / 2;
        this.izquierda = x - ancho / 2;
        this.derecha = x + ancho / 2;
    }

    public boolean estaFueraDePantalla() {
        return x < limiteIzquierdo || x > limiteDerecho;
    }

    public void desactivar() {
        this.activo = false;
    }

    // Retorna la cantidad de vidas que quita al colisionar con la princesa
    public int vidasQueQuita() {
        return 2;
    }
}
