package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego
{
	
	private Entorno entorno;
	Fondo fondo;
	Princesa princesa;
	Isla[][] islas;
	double velocidad;
	
	Juego()
	{
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Proyecto para TP", 800, 600);
		
		double centroX = this.entorno.ancho() / 2.0; // 400.0 
		double centroY = this.entorno.alto() / 2.0; // 300.0
		// Variable para mover la princesa/elementos
		this.velocidad = 4;
		
		this.fondo = new Fondo(centroX, centroY, 1.0, this.entorno);
		this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150; //Mueve el fondo en base a su tamaño
 		
		this.princesa = new Princesa(centroX, centroY, this.entorno);
		
		int[] islaNiveles = {200, 400, 570}; // Posiciones "y" de las islas
		this.islas = new Isla[3][10];
		
		for(int y = 0; y < this.islas.length; y++) {
			for(int x = 0; x < this.islas[0].length; x++) {
				this.islas[y][x] = new Isla(y * 200 + x * 400 + (Math.random() * 100), islaNiveles[y], this.entorno);
			}
		}
		
		this.entorno.iniciar();
	}
	
	public void tick()
	{
		
		// Controlar horizontalmente a la princesa
		// El juego se mueve solo si la princesa lo hace
		if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)|| this.entorno.estaPresionada('d')) {
			this.princesa.moverse(this.velocidad);
			moverNivel(this.princesa, this.islas, this.fondo);
		}
		if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
			this.princesa.moverse(-this.velocidad);
			moverNivel(this.princesa, this.islas, this.fondo);
		}
		// Salto de la princesa
		if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA) || this.entorno.sePresiono('w')) {
			this.princesa.iniciarSalto();
		}
		
		// Gravedad de a princesa
		if(seApoyo(this.princesa, this.islas)) {
			this.princesa.caida = false;
		} else {
			this.princesa.caida = true;
		}
		this.princesa.movVertical();
		
		// Dibujo de elementos
		this.fondo.dibujar();
		
		for(Isla[] fila: this.islas) {
			for(Isla isla: fila) {
				isla.dibujar();
			}
		}
		
		this.princesa.dibujar();
		
	}
	
	// Metodo para calcular  si la princesa esta muy cerca de una isla (colision)
	// Los valores estan ajustados a mano para una mejor colision
	public boolean seApoyo(Princesa p, Isla[][] i) {
		for(Isla[] fila: i) {
			for(Isla isla: fila) {
				if(isla.arriba - p.abajo < 1 && isla.arriba - p.abajo > -3 && isla.derecha - p.izquierda > 10 && p.derecha - isla.izquierda > 10) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Metodo para mover todo los elementos del juego
	public void moverNivel(Princesa p, Isla[][] i, Fondo f) {
		if(p.x > (this.entorno.ancho() * 2/3)) {
			p.x -= this.velocidad;
			f.x -= this.velocidad; 
			for(Isla[] fila: i) {
				for(Isla isla: fila) {
					isla.x -= this.velocidad;
					isla.actualColis();
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Juego juego = new Juego();
	}
}
