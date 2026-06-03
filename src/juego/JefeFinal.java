package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class JefeFinal {
    double x, y;
    double ancho, alto;
    double escala;
    int vidas;
    int vidasMaximas;
    Image imagen;
    boolean activo;
    double arriba, abajo, izquierda, derecha;
    
    int contadorAtaque;
    int intervaloAtaque;
    
    Corazon[] corazonesJefe;
    
    public JefeFinal(double x, double y, Entorno e, int vidasPrincesa) {
        this.x = x;
        this.y = y;
        this.escala = 0.18;
        this.vidasMaximas = vidasPrincesa;
        this.vidas = this.vidasMaximas;
        this.activo = true;
        this.contadorAtaque = 100;
        this.intervaloAtaque = 120; // Tiempo de disparo hacia la princesa
        
        this.imagen = Herramientas.cargarImagen("juego/JefeFinal.png");
        
        if (this.imagen != null) {
            this.ancho = this.imagen.getWidth(null) * this.escala;
            this.alto = this.imagen.getHeight(null) * this.escala;
        } else {
            this.ancho = 40;
            this.alto = 40;
        }
        
        this.corazonesJefe = new Corazon[vidasMaximas];
        for (int i = 0; i < this.corazonesJefe.length; i++) {
            this.corazonesJefe[i] = new Corazon(e.ancho() - 40 - i * 40, 35, 0.08);
        }
        
        actualizarColisiones();
    }
    
    public void dibujar(Entorno e) {
        if (activo && imagen != null) {
            e.dibujarImagen(imagen, x, y, 0, this.escala);
        }
    }
    
    public void dibujarCorazones(Entorno e) {
        if (corazonesJefe != null) {
            for (int i = 0; i < corazonesJefe.length; i++) {
                if (i < vidas) {
                    corazonesJefe[i].activo = true;
                } else {
                    corazonesJefe[i].activo = false;
                }
                corazonesJefe[i].dibujar(e);
            }
        }
    }
    
    public void actualizarColisiones() {
        this.arriba = y - alto / 2;
        this.abajo = y + alto / 2;
        this.izquierda = x - ancho / 2;
        this.derecha = x + ancho / 2;
    }
    
    public void recibirDanio() {
        vidas--;
        if (vidas <= 0) {
            activo = false;
        }
    }
    
    public boolean puedeAtacar() {
        contadorAtaque++;
        if (contadorAtaque >= intervaloAtaque) {
            contadorAtaque = 0;
            return true;
        }
        return false;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void moverHacia(double targetX) {
        if (x < targetX) {
            x += 0.5;
        } else if (x > targetX) {
            x -= 0.5;
        }
        actualizarColisiones();
    }
}