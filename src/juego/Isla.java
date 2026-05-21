package juego;

import java.awt.Image;

import entorno.Entorno;
import entorno.Herramientas;

public class Isla {
	double x, y, escala;
	// variables para calcular el tamaño de la imagen
	double tamArriba, tamAbajo, tamDerecha, tamIzquierda;
	// variables para calcular las colisiones
	double arriba, abajo, izquierda, derecha;
	Image imagen;
	Entorno e;
	
	public Isla(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.escala = 0.4;
		this.imagen = Herramientas.cargarImagen("juego/isla.png");
		this.e = e;
		
		//Calculo del tamaño de la imagen
		this.tamAbajo = -this.imagen.getHeight(null) * this.escala / 2;
		this.tamArriba = this.imagen.getHeight(null) * this.escala / 2;
		this.tamDerecha = -this.imagen.getWidth(null) * this.escala / 2;
		this.tamIzquierda = this.imagen.getWidth(null) * this.escala / 2;
		//calculo de las colisiones
		this.arriba = this.y + tamAbajo;
		this.abajo = this.y + tamArriba;
		this.derecha = this.x + tamDerecha;
		this.izquierda = this.x + tamIzquierda;
	}
	
	public void dibujar() {
		e.dibujarImagen(imagen, this.x, this.y, 0, this.escala);
	}
	
	// metodo que actualiza las collisiones con la posicion actual
	public void actualColis() {
		this.arriba = this.y + tamAbajo;
		this.abajo = this.y + tamArriba;
		this.derecha = this.x + tamDerecha;
		this.izquierda = this.x + tamIzquierda;
	}
}
