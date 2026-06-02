package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {

	double x, y, escala;
	double ancho, alto;
	double arriba, abajo, izquierda, derecha;
	boolean caida, salto;
	int ciclos;
	double velocidadY;
	Entorno e;
	Image imagenIzquierda, imagenDerecha;
	boolean mirandoIzquierda;

	public Princesa(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.e = e;
		this.escala = 0.13;
		this.salto = false;
		this.caida = true;
		this.ciclos = 0;
		this.velocidadY = 0;
		this.imagenIzquierda = Herramientas.cargarImagen("juego/princesaIzq.gif");
		this.imagenDerecha = Herramientas.cargarImagen("juego/princesaDer.gif");

		this.mirandoIzquierda = false;

		// tamaño de la princesa
		if (this.imagenDerecha != null) {
			this.alto = this.imagenDerecha.getHeight(null) * this.escala;
			this.ancho = this.imagenDerecha.getWidth(null) * this.escala;
		} else {
			this.alto = 50;
			this.ancho = 30;
		}

		actualColis();
	}

	public void dibujar() {
		if (mirandoIzquierda && imagenIzquierda != null) {
			this.e.dibujarImagen(this.imagenIzquierda, this.x, this.y, 0, this.escala);
		} else if (imagenDerecha != null) {
			this.e.dibujarImagen(this.imagenDerecha, this.x, this.y, 0, this.escala);
		} else {
			this.e.dibujarRectangulo(this.x, this.y, this.ancho, this.alto, 0, java.awt.Color.RED);
		}
	}

	// movimiento de la princesa
	public void moverse(double velocidad) {
		double proximaX = this.x + velocidad;
		if (proximaX > this.ancho / 2 && proximaX < this.e.ancho() - this.ancho / 2) {
			this.x = proximaX;
			actualColis();
		}

		// detecta si la princesa mira a la izquierda o derecha
		if (velocidad < 0) {
			this.mirandoIzquierda = true;
		}
		if (velocidad > 0) {
			this.mirandoIzquierda = false;
		}
	}

	// movimiento vertical de la princesa
	public void movVertical() {
		int maxCiclos = 20;
		double gravedad = 0.8;
		double fuerzaSalto = -12;

		if (salto && ciclos < maxCiclos) {
			velocidadY = fuerzaSalto;
			ciclos++;
		} else if (salto && ciclos >= maxCiclos) {
			salto = false;
		}

		if (!salto) {
			velocidadY += gravedad;
		}

		// actualiza la posicion de la princesa
		this.y += velocidadY;
		actualColis();

		if (this.y - this.alto / 2 < 0) {
			this.y = this.alto / 2;
			if (velocidadY < 0)
				velocidadY = 0;
		}
	}

	// inicia el salto
	public void iniciarSalto() {
		if (!this.caida && !this.salto) {
			this.salto = true;
			this.ciclos = 0;
			this.velocidadY = -12;
		}
	}

	public void actualColis() {
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}

	// resetea la posicion de la princesa
	public void resetearPosicion(double x, double y) {
		this.x = x;
		this.y = y;
		this.velocidadY = 0;
		this.salto = false;
		this.caida = true;
		this.ciclos = 0;
		actualColis();
	}
}