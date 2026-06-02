package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Enemigo {
    double x, y, escala;
    double ancho, alto;
    double velocidad;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    int limiteIzquierdo;
    int limiteDerecho;

    // Constructor del enemigo
    public Enemigo(double x, double y, double velocidad, int anchoPantalla, int altoPantalla) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.activo = true;
        this.escala = 0.2;

        if (this.velocidad > 1) {
            this.imagen = Herramientas.cargarImagen("juego/enemigoDer.gif");
        } else {
            this.imagen = Herramientas.cargarImagen("juego/enemigoIzq.gif");
        }

        // tamaño del enemigo
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * this.escala;
            this.alto = this.imagen.getHeight(null) * this.escala;
        } else {
            this.ancho = 20;
            this.alto = 20;
        }

        // limites de pantalla para que el enemigo no salga de la pantalla
        this.limiteIzquierdo = -50;
        this.limiteDerecho = anchoPantalla + 50;

        actualizarColisiones();
    }

    public void dibujar(Entorno e) {
        if (activo) {
            if (imagen != null) {
                e.dibujarImagen(imagen, x, y, 0, this.escala);
            } else {
                e.dibujarCirculo(x, y, 15, java.awt.Color.RED);
            }
        }
    }

    // movimiento del enemigo
    public void mover() {
        if (activo) {
            x += velocidad;
            actualizarColisiones();
        }
    }

    public void actualizarColisiones() {
        this.arriba = y - alto / 2;
        this.abajo = y + alto / 2;
        this.izquierda = x - ancho / 2;
        this.derecha = x + ancho / 2;
    }

    // verifica si el enemigo esta fuera de la pantalla
    public boolean estaFueraDePantalla() {
        return x < limiteIzquierdo || x > limiteDerecho;
    }

    public void desactivar() {
        this.activo = false;
    }
}