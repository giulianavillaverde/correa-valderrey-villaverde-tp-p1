package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {
	
	double x, y, escala;
	double ancho, alto; // Variables para calcular el tamaño de la imagen
	double arriba, abajo, izquierda, derecha; // Variables para calcular las colisiones
	Boolean caida, salto; int ciclos; // Variables para la caida y salto de la princesa
	Entorno e;
	Image imagenIzquierda, imagenDerecha;
	boolean mirandoIzquierda; // Variable lógica para saber hacia dónde mirar

	public Princesa(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.e = e;
		this.escala = 0.2; // Ajuste de tamaño
		this.salto = false;
		this.caida = true;
		this.ciclos = 0;
		this.imagenIzquierda = Herramientas.cargarImagen("juego/princesaIzq.gif");
		this.imagenDerecha = Herramientas.cargarImagen("juego/princesaDer.gif");
		
		// Arranca mirando a la izquierda
		this.mirandoIzquierda = true;
		
		// Calculo del tamaño de la imagen
		this.alto = this.imagenDerecha.getHeight(null) * this.escala;
		this.ancho = this.imagenDerecha.getWidth(null) * this.escala;
		
		// Calculo para las colisiones
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}

	public void dibujar() {
		if (this.mirandoIzquierda && this.imagenIzquierda != null) {
			
			this.e.dibujarImagen(this.imagenIzquierda, this.x, this.y, 0, this.escala);
		
		} else if (this.imagenDerecha != null) {		
			
			this.e.dibujarImagen(this.imagenDerecha, this.x, this.y, 0, this.escala);
		}
	}
	
	public void moverse(double velocidad) {
		
		double proximaX = this.x + velocidad;
		if (proximaX > this.ancho && proximaX < this.e.ancho() - this.ancho) { //control de limites, margen izquierdo y margen derecho
			this.x = proximaX;
			
			actualColis();
		}
		
		if (velocidad <= -1) {
			this.mirandoIzquierda = true;
		}
		if (velocidad >= 1) {
			this.mirandoIzquierda = false;
		}
	}
	
	public void movVertical() {
		int maxCiclos = 25;
		// Terminacion del ciclo de salto
		if(ciclos >= maxCiclos) {
			ciclos = 0;
			salto = false;
		}
		// Cuando se salta se empieza un ciclo para mover la princesa hacia arriba
		if(salto && ciclos <= maxCiclos) {
			this.y -= 8;
			ciclos++;
			
			actualColis();
		}
		
		// Bajar a la princesa si esta en caida y no esta en salto
		if(caida && !salto) {
			this.y += 4;
			
			actualColis();
		}
	}
	// Metodo que inicia el salto si la princesa no esta en caida o en un salto
	public void iniciarSalto() {
		if(!this.caida && !this.salto) {
			this.salto = true;
		}
	}
	
	// Metodo que actualiza las collisiones con la posicion actual
	public void actualColis() {
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}
}