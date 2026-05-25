package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Isla {
	double x, y, escala;
	double ancho, alto;
	double arriba, abajo, izquierda, derecha;
	Image imagen;
	Entorno e;
	int tipo; // 1 = grande, 2 = mediana, 3 = chiquita
	
	// Constructor con tipo: 1=grande, 2=mediana, 3=chiquita
	public Isla(double x, double y, Entorno e, int tipo) {
		this.x = x;
		this.y = y;
		this.e = e;
		this.tipo = tipo;
		
		if (tipo == 1) {
			this.escala = 0.5; // Grande
		} else if (tipo == 2) {
			this.escala = 0.35; // Mediana
		} else {
			this.escala = 0.2; // Chiquita
		}
		
		this.imagen = Herramientas.cargarImagen("juego/isla.png");
		
		if (this.imagen != null) {
			this.alto = this.imagen.getHeight(null) * this.escala;
			this.ancho = this.imagen.getWidth(null) * this.escala;
		} else {
			this.alto = 30;
			this.ancho = 80;
		}
		
		actualColis();
	}
	
	// Constructor para compatibilidad
	public Isla(double x, double y, Entorno e) {
		this(x, y, e, 3);
	}
	
	public void dibujar() {
		if (imagen != null) {
			e.dibujarImagen(imagen, this.x, this.y, 0, this.escala);
		} else {
			e.dibujarRectangulo(this.x, this.y, this.ancho, this.alto, 0, java.awt.Color.GREEN);
		}
	}
	
	public void actualColis() {
		this.arriba = this.y - alto / 2;
		this.abajo = this.y + alto / 2;
		this.derecha = this.x + ancho / 2;
		this.izquierda = this.x - ancho / 2;
	}
}