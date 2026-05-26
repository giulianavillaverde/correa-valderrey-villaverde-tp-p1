package juego;

import entorno.Entorno;
import entorno.InterfaceJuego;
import java.awt.Color;

public class Juego extends InterfaceJuego {
    
    private Entorno entorno;
    Fondo fondo;
    Princesa princesa;
    Isla[][] islas;
    Enemigo[] enemigos;
    ProyectilPrincesa proyectil;
    Castillo castillo;
    Corazon[] corazones;
    double velocidad;
    double desplazamiento;
    
    int vidas;
    boolean juegoTerminado;
    boolean victoria;
    double anchoTotalMapa;
    
    int contadorSpawn;
    int intervaloMinEnemigos;
    int minEnemigosPantalla;
    
    int tiempoReaparecer;
    boolean reapareciendo;
    boolean primeraVez;
    
    int tiempoMensajeCastillo;
    boolean mostrarMensajeCastillo;
    
    Juego() {
        this.entorno = new Entorno(this, "Super Elizabeth Sis", 800, 600);
        
        double centroX = this.entorno.ancho() / 2.0;
        this.velocidad = 4;
        this.vidas = 6;
        this.juegoTerminado = false;
        this.victoria = false;
        this.contadorSpawn = 0;
        this.intervaloMinEnemigos = 40;
        this.minEnemigosPantalla = 3;
        this.proyectil = null;
        this.anchoTotalMapa = 8000;
        this.desplazamiento = 0;
        this.reapareciendo = false;
        this.tiempoReaparecer = 0;
        this.primeraVez = true;
        this.tiempoMensajeCastillo = 0;
        this.mostrarMensajeCastillo = false;
        
        this.fondo = new Fondo(centroX, 300, 1.5, this.entorno);
        
        // ==================== CREAR ISLAS CON DISTANCIA PARA SALTAR ====================
        this.islas = new Isla[3][20];
        
        // Nivel 2: islas grandes (piso) - tipo 1, y = 530
        // Distancia entre islas: 400-550 píxeles (permite saltar entre ellas)
        double xPosGrande = 350;
        for(int i = 0; i < 20; i++) {
            xPosGrande = xPosGrande + 420 + Math.random() * 130;
            if (xPosGrande < anchoTotalMapa - 200) {
                this.islas[2][i] = new Isla(xPosGrande, 530, this.entorno, 1);
            }
        }
        
        // Nivel 1: islas medianas - tipo 2, y = 350
        // Distancia entre islas: 380-500 píxeles
        double xPosMediana = 400;
        for(int i = 0; i < 20; i++) {
            xPosMediana = xPosMediana + 400 + Math.random() * 120;
            double yPos = 350 + (Math.random() * 50 - 25);
            if (xPosMediana < anchoTotalMapa - 200) {
                this.islas[1][i] = new Isla(xPosMediana, yPos, this.entorno, 2);
            }
        }
        
        // Nivel 0: islas chiquitas - tipo 3, y = 200
        // Distancia entre islas: 350-480 píxeles
        double xPosChica = 450;
        for(int i = 0; i < 20; i++) {
            xPosChica = xPosChica + 380 + Math.random() * 110;
            double yPos = 200 + (Math.random() * 50 - 25);
            if (xPosChica < anchoTotalMapa - 200) {
                this.islas[0][i] = new Isla(xPosChica, yPos, this.entorno, 3);
            }
        }
        
        // Crear corazones
        this.corazones = new Corazon[6];
        for(int i = 0; i < this.corazones.length; i++) {
            this.corazones[i] = new Corazon(35 + i * 40, 35, 0.08);
        }
        
        this.princesa = new Princesa(centroX, 500, this.entorno);
        this.enemigos = new Enemigo[30];
        
        // Castillo al final del mapa
        this.castillo = new Castillo(anchoTotalMapa - 150, 500, this.entorno);
        
        this.entorno.iniciar();
    }
    
    public void tick() {
        if (primeraVez) {
            primeraVez = false;
            
            // Posicionar princesa sobre la primera isla grande
            for(Isla isla : this.islas[2]) {
                if(isla != null) {
                    this.princesa.x = isla.x - desplazamiento;
                    this.princesa.y = isla.arriba - this.princesa.alto/2;
                    break;
                }
            }
            this.princesa.velocidadY = 0;
            this.princesa.caida = false;
            this.princesa.salto = false;
            this.princesa.actualColis();
        }
        
        if (mostrarMensajeCastillo) {
            tiempoMensajeCastillo--;
            if (tiempoMensajeCastillo <= 0) {
                mostrarMensajeCastillo = false;
            }
        }
        
        if (juegoTerminado) {
            this.entorno.cambiarFont("Arial", 30, Color.WHITE);
            if (victoria) {
                this.entorno.escribirTexto("¡VICTORIA!", this.entorno.ancho()/2 - 70, this.entorno.alto()/2);
                this.entorno.escribirTexto("¡Rescataste a Mario!", this.entorno.ancho()/2 - 130, this.entorno.alto()/2 + 50);
            } else {
                this.entorno.escribirTexto("GAME OVER", this.entorno.ancho()/2 - 80, this.entorno.alto()/2);
                this.entorno.escribirTexto("Presiona R para reiniciar", this.entorno.ancho()/2 - 140, this.entorno.alto()/2 + 50);
                
                if (this.entorno.sePresiono('r') || this.entorno.sePresiono('R')) {
                    reiniciarJuego();
                }
            }
            dibujarTodo();
            return;
        }
        
        if (reapareciendo) {
            tiempoReaparecer--;
            if (tiempoReaparecer <= 0) {
                reapareciendo = false;
                this.princesa.resetearPosicion(this.entorno.ancho() / 2, 50);
                this.princesa.caida = true;
                this.princesa.velocidadY = 0;
                this.princesa.salto = false;
            }
            dibujarTodo();
            return;
        }
        
        // Movimiento horizontal
        if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) || this.entorno.estaPresionada('d')) {
            this.princesa.moverse(this.velocidad);
        }
        if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
            this.princesa.moverse(-this.velocidad);
        }
        
        // Actualizar desplazamiento de cámara
        if (this.princesa.x > this.entorno.ancho() - 200) {
            desplazamiento += this.princesa.x - (this.entorno.ancho() - 200);
            this.princesa.x = this.entorno.ancho() - 200;
        }
        if (this.princesa.x < 200) {
            desplazamiento += this.princesa.x - 200;
            this.princesa.x = 200;
        }
        
        // Limitar desplazamiento
        if (desplazamiento < 0) desplazamiento = 0;
        if (desplazamiento > anchoTotalMapa - this.entorno.ancho()) {
            desplazamiento = anchoTotalMapa - this.entorno.ancho();
        }
        
        // Actualizar posición del fondo
        this.fondo.x = this.entorno.ancho() / 2 - desplazamiento * 0.5;
        
        // Salto
        if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA) || this.entorno.sePresiono('w')) {
            if (!this.princesa.salto && !this.princesa.caida) {
                this.princesa.salto = true;
                this.princesa.ciclos = 0;
                this.princesa.velocidadY = -12;
            }
        }
        
        // Física
        if (!this.princesa.salto) {
            this.princesa.velocidadY += 0.8;
        }
        
        this.princesa.y += this.princesa.velocidadY;
        this.princesa.actualColis();
        
        if (this.princesa.salto) {
            this.princesa.ciclos++;
            if (this.princesa.ciclos > 20) {
                this.princesa.salto = false;
            }
        }
        
        // ==================== COLISIÓN CON ISLAS ====================
        boolean enIsla = false;
        
        for(Isla[] fila: this.islas) {
            if(fila != null) {
                for(Isla isla: fila) {
                    if(isla != null) {
                        double islaXDraw = isla.x - desplazamiento;
                        double islaIzquierda = islaXDraw - isla.ancho/2;
                        double islaDerecha = islaXDraw + isla.ancho/2;
                        
                        boolean colisionHorizontal = this.princesa.derecha > islaIzquierda && this.princesa.izquierda < islaDerecha;
                        double distanciaVertical = Math.abs(this.princesa.abajo - isla.arriba);
                        
                        if (distanciaVertical < 15 && colisionHorizontal && this.princesa.velocidadY >= 0) {
                            this.princesa.y = isla.arriba - this.princesa.alto/2;
                            this.princesa.velocidadY = 0;
                            this.princesa.salto = false;
                            this.princesa.caida = false;
                            this.princesa.actualColis();
                            enIsla = true;
                            break;
                        }
                        
                        distanciaVertical = Math.abs(this.princesa.arriba - isla.abajo);
                        if (distanciaVertical < 15 && colisionHorizontal && this.princesa.velocidadY < 0) {
                            this.princesa.y = isla.abajo + this.princesa.alto/2;
                            this.princesa.velocidadY = 0;
                            this.princesa.salto = false;
                            this.princesa.ciclos = 0;
                            this.princesa.actualColis();
                        }
                    }
                }
            }
            if(enIsla) break;
        }
        
        if (!enIsla) {
            this.princesa.caida = true;
        }
        
        // ==================== DISPARO ====================
        if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO) && proyectil == null && !juegoTerminado) {
            int mouseX = this.entorno.mouseX();
            int mouseY = this.entorno.mouseY();
            
            double dx = mouseX - this.princesa.x;
            double dy = mouseY - this.princesa.y;
            double distancia = Math.sqrt(dx*dx + dy*dy);
            
            if (distancia > 0) {
                dx = dx / distancia;
                dy = dy / distancia;
                
                proyectil = new ProyectilPrincesa(this.princesa.x, this.princesa.y - 20, dx, dy, 
                                          entorno.ancho(), entorno.alto());
            }
        }
        
        if (proyectil != null) {
            proyectil.mover();
            if (proyectil.estaFueraDePantalla()) {
                proyectil = null;
            }
        }
        
        // ==================== CAÍDA AL VACÍO ====================
        if (this.princesa.y > this.entorno.alto() + 100) {
            vidas--;
            actualizarCorazones();
            iniciarReaparicion();
        }
        
        // ==================== VICTORIA ====================
        double castilloXDraw = castillo.x - desplazamiento;
        double castilloIzquierda = castilloXDraw - castillo.ancho/2;
        double castilloDerecha = castilloXDraw + castillo.ancho/2;
        
        boolean colisionCastillo = this.princesa.derecha > castilloIzquierda && this.princesa.izquierda < castilloDerecha &&
                                   this.princesa.abajo > castillo.arriba && this.princesa.arriba < castillo.abajo;
        
        if (colisionCastillo) {
            this.victoria = true;
            this.juegoTerminado = true;
        }
        
        // Mostrar mensaje cuando el castillo está cerca
        if (castilloXDraw > 0 && castilloXDraw < this.entorno.ancho() + 100 && !juegoTerminado) {
            mostrarMensajeCastillo = true;
            tiempoMensajeCastillo = 60;
        }
        
        // ==================== DERROTA ====================
        if (this.vidas <= 0) {
            this.juegoTerminado = true;
            this.victoria = false;
        }
        
        dibujarTodo();
    }
    
    public void actualizarCorazones() {
        for(int i = 0; i < corazones.length; i++) {
            if (i < vidas) {
                corazones[i].activo = true;
            } else {
                corazones[i].activo = false;
            }
        }
    }
    
    public void iniciarReaparicion() {
        reapareciendo = true;
        tiempoReaparecer = 20;
    }
    
    public void reiniciarJuego() {
        this.vidas = 6;
        this.juegoTerminado = false;
        this.victoria = false;
        this.proyectil = null;
        this.contadorSpawn = 0;
        this.reapareciendo = false;
        this.primeraVez = true;
        this.desplazamiento = 0;
        this.mostrarMensajeCastillo = false;
        this.fondo.x = this.entorno.ancho() / 2;
        
        for(int i = 0; i < corazones.length; i++) {
            corazones[i].activo = true;
        }
        
        // Regenerar islas
        this.islas = new Isla[3][20];
        
        double xPosGrande = 350;
        for(int i = 0; i < 20; i++) {
            xPosGrande = xPosGrande + 420 + Math.random() * 130;
            if (xPosGrande < anchoTotalMapa - 200) {
                this.islas[2][i] = new Isla(xPosGrande, 530, this.entorno, 1);
            }
        }
        
        double xPosMediana = 400;
        for(int i = 0; i < 20; i++) {
            xPosMediana = xPosMediana + 400 + Math.random() * 120;
            double yPos = 350 + (Math.random() * 50 - 25);
            if (xPosMediana < anchoTotalMapa - 200) {
                this.islas[1][i] = new Isla(xPosMediana, yPos, this.entorno, 2);
            }
        }
        
        double xPosChica = 450;
        for(int i = 0; i < 20; i++) {
            xPosChica = xPosChica + 380 + Math.random() * 110;
            double yPos = 200 + (Math.random() * 50 - 25);
            if (xPosChica < anchoTotalMapa - 200) {
                this.islas[0][i] = new Isla(xPosChica, yPos, this.entorno, 3);
            }
        }
        
        this.princesa.resetearPosicion(this.entorno.ancho() / 2, 500);
        
        this.castillo = new Castillo(anchoTotalMapa - 150, 500, this.entorno);
        
        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = null;
        }
    }
    
    public void dibujarTodo() {
        this.fondo.dibujar();
        
        // Dibujar islas
        for(Isla[] fila: this.islas) {
            if(fila != null) {
                for(Isla isla: fila) {
                    if(isla != null) {
                        double islaXDraw = isla.x - desplazamiento;
                        if (islaXDraw > -200 && islaXDraw < this.entorno.ancho() + 200) {
                            double originalX = isla.x;
                            isla.x = islaXDraw;
                            isla.dibujar();
                            isla.x = originalX;
                        }
                    }
                }
            }
        }
        
        // Dibujar castillo
        if (castillo != null && castillo.activo) {
            double castilloXDraw = castillo.x - desplazamiento;
            double originalX = castillo.x;
            castillo.x = castilloXDraw;
            castillo.dibujar(this.entorno);
            castillo.x = originalX;
        }
        
        
        
        if (proyectil != null) {
            proyectil.dibujar(this.entorno);
        }
        
        if (!reapareciendo) {
            this.princesa.dibujar();
        }
        
        for(Corazon c : corazones) {
            c.dibujar(this.entorno);
        }
        
        if (mostrarMensajeCastillo && !juegoTerminado) {
            this.entorno.cambiarFont("Arial", 20, Color.YELLOW);
            this.entorno.escribirTexto("¡El castillo está cerca!", this.entorno.ancho()/2 - 100, 100);
        }
        
        if (reapareciendo) {
            this.entorno.cambiarFont("Arial", 20, Color.YELLOW);
            this.entorno.escribirTexto("¡PERDISTE UNA VIDA!", this.entorno.ancho()/2 - 100, this.entorno.alto()/2);
        }
    }
    
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}