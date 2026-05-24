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
    ProyectilEnemigo proyectil;
    Castillo castillo;
    double velocidad;
    
    int vidas;
    boolean juegoTerminado;
    boolean victoria;
    double anchoTotalMapa;
    
    int contadorSpawn;
    int intervaloMinEnemigos;
    int minEnemigosPantalla;
    
    // Variables para la animación de reaparecer
    int tiempoReaparecer;
    boolean reapareciendo;
    boolean primeraVez; // Para el inicio del juego
    
    Juego() {
        this.entorno = new Entorno(this, "Super Elizabeth Sis", 800, 600);
        
        double centroX = this.entorno.ancho() / 2.0;
        this.velocidad = 4;
        this.vidas = 3;
        this.juegoTerminado = false;
        this.victoria = false;
        this.contadorSpawn = 0;
        this.intervaloMinEnemigos = 60;
        this.minEnemigosPantalla = 2;
        this.proyectil = null;
        this.anchoTotalMapa = 8000;
        this.reapareciendo = false;
        this.tiempoReaparecer = 0;
        this.primeraVez = true;
        
        this.fondo = new Fondo(centroX, 300, 1.0, this.entorno);
        this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150;
        
        // Crear islas
        int[] islaNiveles = {200, 400, 530};
        this.islas = new Isla[3][10];
        
        for(int y = 0; y < this.islas.length; y++) {
            for(int x = 0; x < this.islas[0].length; x++) {
                this.islas[y][x] = new Isla(y * 200 + x * 400 + (Math.random() * 150), islaNiveles[y], this.entorno);
            }
        }
        
        // Crear princesa (temporalmente en cualquier posición)
        this.princesa = new Princesa(centroX, 500, this.entorno);
        this.enemigos = new Enemigo[20];
        
        double posicionCastillo = this.anchoTotalMapa - 200;
        this.castillo = new Castillo(posicionCastillo, 520, this.entorno);
        
        this.entorno.iniciar();
    }
    
    public void tick() {
        // En el primer tick, posicionar la princesa bien sobre una isla
        if (primeraVez) {
            primeraVez = false;
            
            // Buscar una isla para posicionar a la princesa
            double islaX = this.entorno.ancho() / 2.0;
            double islaY = 500;
            boolean encontroIsla = false;
            
            // Buscar en el nivel más bajo
            for(Isla isla : this.islas[2]) {
                if(isla != null) {
                    islaX = isla.x;
                    islaY = isla.arriba - this.princesa.alto/2;
                    encontroIsla = true;
                    break;
                }
            }
            
            // Si no encontró, buscar cualquier isla
            if(!encontroIsla) {
                for(Isla[] fila : this.islas) {
                    for(Isla isla : fila) {
                        if(isla != null) {
                            islaX = isla.x;
                            islaY = isla.arriba - this.princesa.alto/2;
                            encontroIsla = true;
                            break;
                        }
                    }
                    if(encontroIsla) break;
                }
            }
            
            this.princesa.x = islaX;
            this.princesa.y = islaY;
            this.princesa.velocidadY = 0;
            this.princesa.caida = false;
            this.princesa.salto = false;
            this.princesa.actualColis();
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
        
        // ==================== MANEJO DE REAPARECER ====================
        if (reapareciendo) {
            tiempoReaparecer--;
            if (tiempoReaparecer <= 0) {
                reapareciendo = false;
                // Reaparecer desde arriba del cielo (cayendo)
                this.princesa.resetearPosicion(this.entorno.ancho() / 2, 50);
                this.princesa.caida = true;
                this.princesa.velocidadY = 0;
                this.princesa.salto = false;
            }
            dibujarTodo();
            return;
        }
        
        // ==================== MOVIMIENTO HORIZONTAL ====================
        if (this.entorno.estaPresionada(this.entorno.TECLA_DERECHA) || this.entorno.estaPresionada('d')) {
            this.princesa.moverse(this.velocidad);
            moverNivel();
        }
        if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
            this.princesa.moverse(-this.velocidad);
            moverNivel();
        }
        
        // ==================== SALTO ====================
        if (this.entorno.sePresiono(this.entorno.TECLA_ARRIBA) || this.entorno.sePresiono('w')) {
            if (!this.princesa.salto && !this.princesa.caida) {
                this.princesa.salto = true;
                this.princesa.ciclos = 0;
                this.princesa.velocidadY = -12;
            }
        }
        
        // ==================== FÍSICA ====================
        if (!this.princesa.salto) {
            this.princesa.velocidadY += 0.8;
        }
        
        double yAnterior = this.princesa.y;
        
        this.princesa.y += this.princesa.velocidadY;
        this.princesa.actualColis();
        
        if (this.princesa.salto) {
            this.princesa.ciclos++;
            if (this.princesa.ciclos > 20) {
                this.princesa.salto = false;
            }
        }
        
        // ==================== COLISIÓN CON ISLAS POR ARRIBA ====================
        for(Isla[] fila: this.islas) {
            for(Isla isla: fila) {
                if (this.princesa.arriba <= isla.abajo && 
                    yAnterior - this.princesa.alto/2 > isla.abajo &&
                    this.princesa.derecha > isla.izquierda + 10 &&
                    this.princesa.izquierda < isla.derecha - 10) {
                    
                    this.princesa.y = isla.abajo + this.princesa.alto/2;
                    this.princesa.velocidadY = 0;
                    this.princesa.salto = false;
                    this.princesa.ciclos = 0;
                    this.princesa.actualColis();
                }
            }
        }
        
        // ==================== COLISIÓN CON ISLAS (APOYO) ====================
        boolean enIsla = false;
        
        for(Isla[] fila: this.islas) {
            for(Isla isla: fila) {
                if (this.princesa.abajo >= isla.arriba - 5 && 
                    this.princesa.abajo <= isla.arriba + 15 && 
                    this.princesa.derecha > isla.izquierda + 5 && 
                    this.princesa.izquierda < isla.derecha - 5 &&
                    this.princesa.velocidadY >= 0) {
                    
                    this.princesa.y = isla.arriba - this.princesa.alto/2;
                    this.princesa.velocidadY = 0;
                    this.princesa.salto = false;
                    this.princesa.caida = false;
                    this.princesa.actualColis();
                    enIsla = true;
                    break;
                }
            }
            if(enIsla) break;
        }
        
        if (!enIsla) {
            this.princesa.caida = true;
        }
        
        // ==================== MOVER ENEMIGOS ====================
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null && enemigos[i].activo) {
                enemigos[i].mover();
                if (enemigos[i].estaFueraDePantalla()) {
                    enemigos[i] = null;
                }
            }
        }
        
        // ==================== SPAWN DE ENEMIGOS ====================
        int enemigosActivos = 0;
        for (Enemigo e : enemigos) {
            if (e != null && e.activo) enemigosActivos++;
        }
        
        contadorSpawn++;
        if (contadorSpawn >= intervaloMinEnemigos && enemigosActivos < minEnemigosPantalla + 3) {
            int posicionLibre = -1;
            for (int i = 0; i < enemigos.length; i++) {
                if (enemigos[i] == null) {
                    posicionLibre = i;
                    break;
                }
            }
            
            if (posicionLibre != -1) {
                int lado = (int)(Math.random() * 2);
                double x, vel;
                double yEnemigo = 100 + Math.random() * 400;
                
                if (lado == 0) {
                    x = -30;
                    vel = 2 + Math.random() * 3;
                } else {
                    x = entorno.ancho() + 30;
                    vel = -2 - Math.random() * 3;
                }
                
                enemigos[posicionLibre] = new Enemigo(x, yEnemigo, vel, entorno.ancho(), entorno.alto());
            }
            
            contadorSpawn = 0;
            intervaloMinEnemigos = 40 + (int)(Math.random() * 50);
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
                
                proyectil = new ProyectilEnemigo(this.princesa.x, this.princesa.y - 20, dx, dy, 
                                          entorno.ancho(), entorno.alto());
            }
        }
        
        if (proyectil != null) {
            proyectil.mover();
            if (proyectil.estaFueraDePantalla()) {
                proyectil = null;
            }
        }
        
        // ==================== COLISIÓN PRINCESA - ENEMIGO ====================
        for (int i = 0; i < enemigos.length; i++) {
            Enemigo e = enemigos[i];
            if (e != null && e.activo) {
                if (!(princesa.abajo <= e.arriba || princesa.arriba >= e.abajo || 
                      princesa.derecha <= e.izquierda || princesa.izquierda >= e.derecha)) {
                    vidas--;
                    enemigos[i] = null;
                    iniciarReaparicion();
                    break;
                }
            }
        }
        
        // ==================== COLISIÓN PROYECTIL - ENEMIGO ====================
        if (proyectil != null && proyectil.activo) {
            for (int i = 0; i < enemigos.length; i++) {
                Enemigo e = enemigos[i];
                if (e != null && e.activo) {
                    if (!(proyectil.abajo <= e.arriba || proyectil.arriba >= e.abajo || 
                          proyectil.derecha <= e.izquierda || proyectil.izquierda >= e.derecha)) {
                        proyectil = null;
                        enemigos[i] = null;
                        break;
                    }
                }
            }
        }
        
        // ==================== CAÍDA AL VACÍO ====================
        if (this.princesa.y > this.entorno.alto() + 100) {
            vidas--;
            iniciarReaparicion();
        }
        
        // ==================== VICTORIA ====================
        if (castillo != null && castillo.activo) {
            if (!(princesa.abajo <= castillo.arriba || princesa.arriba >= castillo.abajo || 
                  princesa.derecha <= castillo.izquierda || princesa.izquierda >= castillo.derecha)) {
                this.victoria = true;
                this.juegoTerminado = true;
            }
        }
        
        // ==================== DERROTA ====================
        if (this.vidas <= 0) {
            this.juegoTerminado = true;
            this.victoria = false;
        }
        
        // ==================== DIBUJAR ====================
        dibujarTodo();
    }
    
    public void iniciarReaparicion() {
        reapareciendo = true;
        tiempoReaparecer = 20;
    }
    
    public void reiniciarJuego() {
        this.vidas = 3;
        this.juegoTerminado = false;
        this.victoria = false;
        this.proyectil = null;
        this.contadorSpawn = 0;
        this.reapareciendo = false;
        this.primeraVez = true;
        this.fondo.x = this.entorno.ancho() / 2.0;
        
        // Reiniciar islas
        int[] islaNiveles2 = {200, 400, 530};
        for(int y = 0; y < this.islas.length; y++) {
            for(int x = 0; x < this.islas[0].length; x++) {
                this.islas[y][x].x = y * 200 + x * 400 + (Math.random() * 150);
                this.islas[y][x].y = islaNiveles2[y];
                this.islas[y][x].actualColis();
            }
        }
        
        // La princesa se posicionará en el primer tick
        this.princesa.resetearPosicion(this.entorno.ancho() / 2, 500);
        
        double posicionCastillo2 = this.anchoTotalMapa - 200;
        this.castillo = new Castillo(posicionCastillo2, 520, this.entorno);
        
        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = null;
        }
    }
    
    public void dibujarTodo() {
        this.fondo.dibujar();
        
        for(Isla[] fila: this.islas) {
            for(Isla isla: fila) {
                isla.dibujar();
            }
        }
        
        if (castillo != null && castillo.activo) {
            castillo.dibujar(this.entorno);
        }
        
        for(Enemigo e: enemigos) {
            if (e != null && e.activo) {
                e.dibujar(this.entorno);
            }
        }
        
        if (proyectil != null) {
            proyectil.dibujar(this.entorno);
        }
        
        if (!reapareciendo) {
            this.princesa.dibujar();
        }
        
        this.entorno.cambiarFont("Arial", 18, Color.WHITE);
        this.entorno.escribirTexto("Vidas: " + this.vidas, 20, 40);
        
        if (castillo != null && !juegoTerminado) {
            int distancia = (int)((castillo.x - this.fondo.x) / 10);
            if (distancia > 0) {
                this.entorno.escribirTexto("Distancia al castillo: " + distancia + "m", 20, 70);
            }
        }
        
        if (reapareciendo) {
            this.entorno.cambiarFont("Arial", 20, Color.YELLOW);
            this.entorno.escribirTexto("¡PERDISTE UNA VIDA!", this.entorno.ancho()/2 - 100, this.entorno.alto()/2);
        }
    }
    
    public void moverNivel() {
        if(princesa.x > (this.entorno.ancho() * 2/3)) {
            princesa.x -= this.velocidad;
            fondo.x -= this.velocidad;
            
            for(Isla[] fila: islas) {
                for(Isla isla: fila) {
                    isla.x -= this.velocidad;
                    isla.actualColis();
                }
            }
            
            if(castillo != null) {
                castillo.x -= this.velocidad;
                castillo.actualizarColisiones();
            }
        }
    }
    
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}