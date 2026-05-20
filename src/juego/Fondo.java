package juego;

import java.awt.Image;
import entorno.Entorno;
import entorno.Herramientas;

public class Fondo {
	double x, y, escala;
	Image imagenFondo;
	Entorno e;
	
	public Fondo(double x, double y, double escala, Entorno e) {
		this.x = x;
		this.y = y;
		this.escala = escala;
		this.imagenFondo = Herramientas.cargarImagen("juego/fondonuevo.png");
		this.e = e;
	}
	
	public void dibujar() {
		e.dibujarImagen(imagenFondo, this.x, this.y, 0, this.escala);
	}
	
} 
