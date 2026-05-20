package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego
{

	private Entorno entorno;
	Fondo fondo;
	princesa princesa; 
	Juego()
	{
		// Inicializa el objeto entorno
		this.entorno = new Entorno(this, "Proyecto para TP", 800, 600);
		
			double centroX = 800 / 2.0; // da 400.0 
			double centroY = 600 / 2.0; // da 300.0 
			
			this.fondo = new Fondo(centroX, centroY, 1.0, this.entorno);
			this.princesa = new princesa(centroX, 490, this.entorno);
			
		
		this.entorno.iniciar();
	}
	
	public void tick()
	{
		
		if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA)|| this.entorno.estaPresionada('d')) {
			this.princesa.moverse(1);
		}
		
		if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
				this.princesa.moverse(-1);
	}

		// dibujo de elementos
		this.fondo.dibujar();
		
		this.princesa.dibujar();
		
	}
	

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Juego juego = new Juego();
	}
}
