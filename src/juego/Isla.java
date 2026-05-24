package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Isla {
	double x, y, escala;
	double ancho, alto; // Variables para calcular el tamaño de la imagen
	double arriba, abajo, izquierda, derecha; // Variables para calcular las colisiones
	Image imagen;
	Entorno e;
	
	public Isla(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.escala = 0.4;
		this.imagen = Herramientas.cargarImagen("juego/isla.png");
		this.e = e;
		
		// Calculo del tamaño de la imagen
		this.alto = this.imagen.getHeight(null) * this.escala;
		this.ancho = this.imagen.getWidth(null) * this.escala;
		
		// Calculo de las colisiones
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}
	
	public void dibujar() {
		e.dibujarImagen(imagen, this.x, this.y, 0, this.escala);
	}
	
	// Metodo que actualiza las collisiones con la posicion actual
	public void actualColis() {
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}
}
