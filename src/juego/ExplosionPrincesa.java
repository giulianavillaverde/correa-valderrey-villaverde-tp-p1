package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class ExplosionPrincesa {
	double x, y, escala, tiempo;
	double ancho, alto;
	double arriba, abajo, izquierda, derecha;
	boolean fin;
	Entorno e;
	Image imagen;
	public ExplosionPrincesa(double x, double y, Entorno e) {
		this.tiempo = 0;
		this.x = x;
		this.y = y;
		this.e = e;
		this.escala = 0;
		this.fin = false;
		this.imagen = Herramientas.cargarImagen("juego/explosion.png");
		
		actualizarColisiones();
	}
	
	public void dibujar(Entorno e) {
		e.dibujarImagen(this.imagen, this.x, this.y, 0, this.escala);
	}
	
	public void actualizarColisiones() { //Alto y ancho aqui porque se modifican para la animacioon
		this.alto = this.imagen.getHeight(null) * this.escala;
		this.ancho = this.imagen.getWidth(null) * this.escala;
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}
	// Metodo que mueve y escala la explosion hasta un limite para simular animacion
	public void mover(double x, double y) {
		if (this.tiempo <= 15) {
			this.tiempo++;
			this.escala += 0.5;
		} else if(this.tiempo <= 20) { // Congelar un momento la animacion
			this.tiempo++;
		} else { // Desaparecer la explocion
			this.fin = true;
		}
		this.x = x;
		this.y = y;
		actualizarColisiones();
	}
}