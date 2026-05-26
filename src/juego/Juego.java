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
        this.anchoTotalMapa = 3500;
        this.reapareciendo = false;
        this.tiempoReaparecer = 0;
        this.primeraVez = true;
        
        this.fondo = new Fondo(centroX, 300, 1.5, this.entorno);
        this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150;
        
        // ==================== CREAR ISLAS ====================
        this.islas = new Isla[3][15];
        
        // NIVEL 2: ISLAS GRANDES (piso)
        double acumuladorX = 300;
        double ultimaXGrande = 0;
        for(int i = 0; i < 10; i++) {
            double separacion = 480 + Math.random() * 120;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                this.islas[2][i] = new Isla(xPos, 530, this.entorno, 1);
                acumuladorX = xPos;
                ultimaXGrande = xPos;
            }
        }
        
        // NIVEL 1: ISLAS MEDIANAS
        acumuladorX = 350;
        for(int i = 0; i < 10; i++) {
            double separacion = 450 + Math.random() * 110;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                double yPos = 350 + (Math.random() * 50 - 25);
                this.islas[1][i] = new Isla(xPos, yPos, this.entorno, 2);
                acumuladorX = xPos;
            }
        }
        
        // NIVEL 0: ISLAS CHIQUITAS
        acumuladorX = 400;
        for(int i = 0; i < 10; i++) {
            double separacion = 430 + Math.random() * 100;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                double yPos = 200 + (Math.random() * 50 - 25);
                this.islas[0][i] = new Isla(xPos, yPos, this.entorno, 3);
                acumuladorX = xPos;
            }
        }
        
        // ==================== CASTILLO ====================
        double posicionCastillo = ultimaXGrande + 350;
        if (posicionCastillo > anchoTotalMapa - 150) {
            posicionCastillo = anchoTotalMapa - 150;
        }
        this.castillo = new Castillo(posicionCastillo, 530, this.entorno);
        
        // Crear corazones
        this.corazones = new Corazon[6];
        for(int i = 0; i < this.corazones.length; i++) {
            this.corazones[i] = new Corazon(35 + i * 40, 35, 0.08);
        }
        
        this.princesa = new Princesa(centroX, 500, this.entorno);
        this.enemigos = new Enemigo[30];
        
        this.entorno.iniciar();
    }
    
    public void tick() {
        if (primeraVez) {
            primeraVez = false;
            
            // Posicionar princesa sobre la primera isla grande
            for(Isla isla : this.islas[2]) {
                if(isla != null) {
                    this.princesa.x = isla.x;
                    this.princesa.y = isla.arriba - this.princesa.alto/2;
                    break;
                }
            }
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
            moverNivel();
        }
        if (this.entorno.estaPresionada(this.entorno.TECLA_IZQUIERDA) || this.entorno.estaPresionada('a')) {
            this.princesa.moverse(-this.velocidad);
            moverNivel();
        }
        
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
            for(Isla isla: fila) {
                if(isla != null) {
                    boolean colisionHorizontal = this.princesa.derecha > isla.izquierda && this.princesa.izquierda < isla.derecha;
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
            if(enIsla) break;
        }
        
        if (!enIsla) {
            this.princesa.caida = true;
        }
        
        // ==================== ENEMIGOS (con colisión de islas) ====================
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null && enemigos[i].activo) {
                enemigos[i].moverConIslas(this.islas);
                if (enemigos[i].estaFueraDePantalla()) {
                    enemigos[i] = null;
                }
            }
        }
        
        int enemigosActivos = 0;
        for (Enemigo e : enemigos) {
            if (e != null && e.activo) enemigosActivos++;
        }
        
        if (enemigosActivos < minEnemigosPantalla) {
            int cuantosFaltan = minEnemigosPantalla - enemigosActivos;
            for (int j = 0; j < cuantosFaltan; j++) {
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
                    double yEnemigo = 80 + Math.random() * 450;
                    
                    if (lado == 0) {
                        x = -30;
                        vel = 2 + Math.random() * 3;
                    } else {
                        x = entorno.ancho() + 30;
                        vel = -2 - Math.random() * 3;
                    }
                    
                    enemigos[posicionLibre] = new Enemigo(x, yEnemigo, vel, entorno.ancho(), entorno.alto());
                }
            }
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
        
        // ==================== COLISIÓN PRINCESA - ENEMIGO ====================
        for (int i = 0; i < enemigos.length; i++) {
            Enemigo e = enemigos[i];
            if (e != null && e.activo) {
                if (!(princesa.abajo <= e.arriba || princesa.arriba >= e.abajo || 
                      princesa.derecha <= e.izquierda || princesa.izquierda >= e.derecha)) {
                    vidas--;
                    enemigos[i] = null;
                    actualizarCorazones();
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
            actualizarCorazones();
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
        this.fondo.x = this.entorno.ancho() / 2.0;
        
        for(int i = 0; i < corazones.length; i++) {
            corazones[i].activo = true;
        }
        
        // Regenerar islas
        this.islas = new Isla[3][15];
        
        double acumuladorX = 300;
        double ultimaXGrande = 0;
        for(int i = 0; i < 10; i++) {
            double separacion = 480 + Math.random() * 120;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                this.islas[2][i] = new Isla(xPos, 530, this.entorno, 1);
                acumuladorX = xPos;
                ultimaXGrande = xPos;
            }
        }
        
        acumuladorX = 350;
        for(int i = 0; i < 10; i++) {
            double separacion = 450 + Math.random() * 110;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                double yPos = 350 + (Math.random() * 50 - 25);
                this.islas[1][i] = new Isla(xPos, yPos, this.entorno, 2);
                acumuladorX = xPos;
            }
        }
        
        acumuladorX = 400;
        for(int i = 0; i < 10; i++) {
            double separacion = 430 + Math.random() * 100;
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                double yPos = 200 + (Math.random() * 50 - 25);
                this.islas[0][i] = new Isla(xPos, yPos, this.entorno, 3);
                acumuladorX = xPos;
            }
        }
        
        double posicionCastillo = ultimaXGrande + 350;
        if (posicionCastillo > anchoTotalMapa - 150) {
            posicionCastillo = anchoTotalMapa - 150;
        }
        this.castillo = new Castillo(posicionCastillo, 530, this.entorno);
        
        this.princesa.resetearPosicion(this.entorno.ancho() / 2, 500);
        
        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = null;
        }
    }
    
    public void dibujarTodo() {
        this.fondo.dibujar();
        
        for(Isla[] fila: this.islas) {
            for(Isla isla: fila) {
                if(isla != null) {
                    isla.dibujar();
                }
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
        
        for(Corazon c : corazones) {
            c.dibujar(this.entorno);
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
                    if(isla != null) {
                        isla.x -= this.velocidad;
                        isla.actualColis();
                    }
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