package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class princesa {
	
	double x;
	double y;
	double velocidad;
	Entorno e;
	

	Image imagenIzquierda;
	Image imagenDerecha;
	
	// Variable lógica para saber hacia dónde mirar
	boolean mirandoIzquierda;

	public princesa(double x, double y, Entorno e) {
		this.x = x;
		this.y = y;
		this.velocidad = 4.0;
		this.e = e;
		
		
		this.imagenIzquierda = Herramientas.cargarImagen("juego/princesa.gif");
		this.imagenDerecha = Herramientas.cargarImagen("juego/princesaderecha.gif");
		
		// Arranca mirando a la izquierda como el gif original
		this.mirandoIzquierda = true;
	}

	public void dibujar() {
		double escalaChica = 0.4; // Ajuste de tamaño solicitado
		
		
		if (this.mirandoIzquierda) {
			if (this.imagenIzquierda != null) {
				
				this.e.dibujarImagen(this.imagenIzquierda, this.x, this.y, 0, escalaChica);
			}
		} else {
			if (this.imagenDerecha != null) {
				
				this.e.dibujarImagen(this.imagenDerecha, this.x, this.y, 0, escalaChica);
			}
		}
	}

	public void moverse(int direccion) {
		
		double proximaX = this.x + (direccion * this.velocidad);
		if (proximaX > 30 && proximaX < 770) { //control de limites, 30 es el margen izquierdo y 770 es el margen derecho
			this.x = proximaX;
			
		}
		
		
		if (direccion == -1) {
			this.mirandoIzquierda = true;
		}
		if (direccion == 1) {
			this.mirandoIzquierda = false;
		}
	}
}