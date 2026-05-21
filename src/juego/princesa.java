package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Princesa {
	
	double x;
	double y;
	double escala;
	// variables para calcular el tamaño de la imagen
	double tamArriba, tamAbajo, tamDerecha, tamIzquierda;
	// variables para calcular las colisiones
	double arriba, abajo, izquierda, derecha;
	// variables para la caida y salto de la princesa
	Boolean caida, salto;
	int ciclos;
	
	Entorno e;
	
	Image imagenIzquierda;
	Image imagenDerecha;
	
	// Variable lógica para saber hacia dónde mirar
	boolean mirandoIzquierda;

	public Princesa(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.e = e;
		this.escala = 0.4; // Ajuste de tamaño
		this.salto = false;
		this.caida = true;
		this.ciclos = 0;
		this.imagenIzquierda = Herramientas.cargarImagen("juego/princesaIzq.gif");
		this.imagenDerecha = Herramientas.cargarImagen("juego/princesaDer.gif");
		
		// Arranca mirando a la izquierda
		this.mirandoIzquierda = true;
		
		//Calculo del tamaño de la imagen
		this.tamAbajo = -this.imagenDerecha.getHeight(null) * this.escala / 2;
		this.tamArriba = this.imagenDerecha.getHeight(null) * this.escala / 2;
		this.tamDerecha = -this.imagenDerecha.getWidth(null) * this.escala / 2;
		this.tamIzquierda = this.imagenDerecha.getWidth(null) * this.escala / 2;
		//calculo de las colisiones, cada que una de estos esten en algun metodo es para actualizarlos
		this.arriba = this.y + tamAbajo;
		this.abajo = this.y + tamArriba;
		this.derecha = this.x + tamDerecha;
		this.izquierda = this.x + tamIzquierda;
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
		if (proximaX > 50 && proximaX < 750) { //control de limites, margen izquierdo y margen derecho
			this.x = proximaX;
			
			this.derecha = this.x + tamDerecha;
			this.izquierda = this.x + tamIzquierda;	
		}
		
		if (velocidad <= -1) {
			this.mirandoIzquierda = true;
		}
		if (velocidad >= 1) {
			this.mirandoIzquierda = false;
		}
	}
	
	public void movVertical() {
		int maxCiclos = 20;
		//Terminacion del ciclo de salto
		if(ciclos >= maxCiclos) {
			ciclos = 0;
			salto = false;
		}
		//Cuando se salta se empieza un ciclo para mover la princesa hacia arriba
		if(salto && ciclos <= maxCiclos) {
			this.y -= 8;
			ciclos++;
			
			this.arriba = this.y + tamAbajo;
			this.abajo = this.y + tamArriba;
		}
		
		//Parte para hacer bajar a la princesa si esta en caida y no esta en salto
		if(caida && !salto) {
			this.y += 4;
			this.arriba = this.y + tamAbajo;
			this.abajo = this.y + tamArriba;
		}
	}
	//Metodo que inicia el salto si la princesa no esta en caida o en un salto
	public void iniciarSalto() {
		if(!this.caida && !this.salto) {
			this.salto = true;
		}
	}
}