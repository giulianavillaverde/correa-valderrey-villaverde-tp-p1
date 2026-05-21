package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego
{

	private Entorno entorno;
	Fondo fondo;
	Princesa princesa;
	Isla islas;
	double velocidad;
	
	Juego()
	{
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Proyecto para TP", 800, 600);
		
		double centroX = 800 / 2.0; // da 400.0 
		double centroY = 600 / 2.0; // da 300.0
		this.velocidad = 4;
		
		this.fondo = new Fondo(centroX, centroY, 1.0, this.entorno);
		this.princesa = new Princesa(centroX, centroY, this.entorno);
			
		this.islas = new Isla(centroX, 550, this.entorno);
		
		this.entorno.iniciar();
	}
	
	public void tick()
	{
		
		//Controlar horizontalmente a la princesa
		if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)|| this.entorno.estaPresionada('d')) {
			this.princesa.moverse(this.velocidad);
		}
		if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
				this.princesa.moverse(-this.velocidad);
		}
		//Salto de la princesa
		if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA) || this.entorno.sePresiono('w')) {
			this.princesa.iniciarSalto();
		}
		
		//Gravedad de a princesa
		if(seApoyo(this.princesa, this.islas)) {
			this.princesa.caida = false;
		} else {
			this.princesa.caida = true;
		}
		this.princesa.movVertical();

		// dibujo de elementos
		this.fondo.dibujar();
		
		this.islas.dibujar();

		this.princesa.dibujar();
		
	}
	
	//Metodo para saber si la princesa esta muy cerca de una isla (colision)
	//Los valores estan ajustados a mano y ojo para una mejor colision
	public boolean seApoyo(Princesa p, Isla i) {
		return p.abajo - i.arriba > -1 && p.abajo - i.arriba < 10 && p.derecha - i.izquierda < -30 && p.izquierda - i.derecha > 30;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Juego juego = new Juego();
	}
}
