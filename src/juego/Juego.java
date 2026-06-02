package juego;

import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;
import java.awt.Color;
import java.awt.Image;

public class Juego extends InterfaceJuego {

    private Entorno entorno;
    Fondo fondo;
    Princesa princesa;
    Isla[][] islas;
    Enemigo[] enemigos;
    Enemigo2[] enemigos2;
    ProyectilPrincesa proyectil;
    ExplosionPrincesa explosion;
    Castillo castillo;
    Corazon[] corazones;
    PocionVida[] pociones;
    double velocidad;
    Image fondoMenu;
    Image botonJugar;
    Image botonReiniciar;
    Image titulo;
    Image gameOverImage;
    Image victoriaImage;

    int vidas;
    boolean juegoTerminado;
    boolean victoria;
    boolean menuActivo;
    double anchoTotalMapa;

    int contadorSpawn;
    int intervaloMinEnemigos;
    int minEnemigosPantalla;

    int tiempoReaparecer;
    boolean reapareciendo;
    boolean primeraVez;

    int tiempoExplosion;
    String mensajeVidas;
    // Variables para items
    int enemigosEliminados;
    int enemigosParaItem;

    // Posiciones de los botones
    double botonJugarX, botonJugarY;
    double botonReiniciarX, botonReiniciarY;
    double botonAncho, botonAlto;

    Juego() {
        this.entorno = new Entorno(this, "Super Elizabeth Sis", 800, 600);

        double centroX = this.entorno.ancho() / 2.0;
        this.velocidad = 3;
        this.vidas = 6;
        this.juegoTerminado = false;
        this.victoria = false;
        this.menuActivo = true;
        this.contadorSpawn = 0;
        this.intervaloMinEnemigos = 40;
        this.minEnemigosPantalla = 3;
        this.proyectil = null;
        this.anchoTotalMapa = 4000;
        this.reapareciendo = false;
        this.tiempoReaparecer = 0;
        this.primeraVez = true;
        this.tiempoExplosion = 0;
        this.mensajeVidas = "¡PERDISTE UNA VIDA!";
        // Inicia las pociones
        this.pociones = new PocionVida[10];
        this.enemigosEliminados = 0;
        this.enemigosParaItem = 4;

        // Cargar imágenes
        this.fondoMenu = Herramientas.cargarImagen("juego/menu.jpg");
        this.botonJugar = Herramientas.cargarImagen("juego/jugar.png");
        this.botonReiniciar = Herramientas.cargarImagen("juego/reiniciar.png");
        this.titulo = Herramientas.cargarImagen("juego/titulo.png");
        this.gameOverImage = Herramientas.cargarImagen("juego/gameover.png");
        this.victoriaImage = Herramientas.cargarImagen("juego/victoria.png");

        // Tamaño del área de click
        this.botonAncho = 120;
        this.botonAlto = 50;
        
        // Posiciones
        this.botonJugarX = centroX - botonAncho / 2;
        this.botonJugarY = this.entorno.alto() / 2 - 30;
        this.botonReiniciarX = centroX - botonAncho / 2;
        this.botonReiniciarY = this.entorno.alto() / 2 + 30;

        this.fondo = new Fondo(centroX, 300, 1.5, this.entorno);
        this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150;

        // CREACIÓN DE ISLAS
        this.islas = new Isla[3][15];

        // NIVEL 2: ISLAS GRANDES (piso)
        double acumuladorX = 100;
        for (int i = 0; i < 10; i++) {
            double separacion;
            if (i == 0) {
                separacion = 250 + Math.random() * 80;
            } else {
                separacion = 560 + Math.random() * 50;
            }
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                this.islas[2][i] = new Isla(xPos, 560, this.entorno, 1);
                acumuladorX = xPos;
            }
        }

        // NIVEL 1: ISLAS MEDIANAS
        acumuladorX = 150;
        double ultimaXMediana = 0;
        for (int i = 0; i < 10; i++) {
            double separacion;
            if (i == 0) {
                separacion = 220 + Math.random() * 80;
            } else {
                separacion = 480 + Math.random() * 70;
            }
            double xPos = acumuladorX + separacion;
            if (xPos < anchoTotalMapa - 400) {
                this.islas[1][i] = new Isla(xPos, 340, this.entorno, 2);
                acumuladorX = xPos;
                ultimaXMediana = xPos;
            }
        }

        // NIVEL 0: ISLAS CHIQUITAS
        acumuladorX = 200;
        for (int j = 0; j < 10; j++) {
            double separacionMinima;
            if (j == 0) {
                separacionMinima = 200 + Math.random() * 80;
            } else {
                separacionMinima = 260 + Math.random() * 60;
            }
            double xPos = acumuladorX + separacionMinima;
            if (xPos < anchoTotalMapa - 400) {
                this.islas[0][j] = new Isla(xPos, 120, this.entorno, 3);
                acumuladorX = xPos;
            }
        }

        // CASTILLO
        double posicionCastillo = ultimaXMediana;
        if (posicionCastillo > anchoTotalMapa - 150) {
            posicionCastillo = anchoTotalMapa - 150;
        }
        this.castillo = new Castillo(posicionCastillo, 250, this.entorno);

        // Crear corazones
        this.corazones = new Corazon[6];
        for (int k = 0; k < this.corazones.length; k++) {
            this.corazones[k] = new Corazon(35 + k * 40, 35, 0.08);
        }

        this.princesa = new Princesa(centroX, 500, this.entorno);
        this.enemigos = new Enemigo[30];
        this.enemigos2 = new Enemigo2[15];

        this.entorno.iniciar();
    }

    public void tick() {
        // ==================== MENÚ DE INICIO ====================
        if (menuActivo) {
            dibujarMenu();

            if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO)) {
                int mouseX = this.entorno.mouseX();
                int mouseY = this.entorno.mouseY();

                if (mouseX >= botonJugarX && mouseX <= botonJugarX + botonAncho &&
                    mouseY >= botonJugarY && mouseY <= botonJugarY + botonAlto) {
                    menuActivo = false;
                    iniciarPartida();
                }
            }

            if (this.entorno.sePresiono('r') || this.entorno.sePresiono('R')) {
                menuActivo = false;
                iniciarPartida();
            }
            return;
        }

        // ==================== PANTALLA DE FIN DE JUEGO ====================
        if (juegoTerminado) {
            dibujarFinJuego();
            return;
        }

        if (primeraVez) {
            primeraVez = false;
            for (Isla isla : this.islas[2]) {
                if (isla != null) {
                    this.princesa.x = isla.x;
                    this.princesa.y = isla.arriba - this.princesa.alto / 2;
                    break;
                }
            }
            this.princesa.velocidadY = 0;
            this.princesa.caida = false;
            this.princesa.salto = false;
            this.princesa.actualColis();
        }

        // Reaparecer después de perder una vida
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

        // Gravedad
        if (!this.princesa.salto) {
            this.princesa.velocidadY += 0.8;
        }

        this.princesa.y += this.princesa.velocidadY;
        this.princesa.actualColis();

        if (this.princesa.salto) {
            this.princesa.ciclos++;
            if (this.princesa.ciclos > 20 || this.princesa.arriba <= 90) {
                this.princesa.salto = false;
            }
        }

        // Colisión con islas
        boolean enIsla = false;
        for (Isla[] fila : this.islas) {
            for (Isla isla : fila) {
                if (isla != null) {
                    boolean colisionHorizontal = this.princesa.derecha > isla.izquierda
                            && this.princesa.izquierda < isla.derecha;
                    double distanciaVertical = Math.abs(this.princesa.abajo - isla.arriba);

                    if (distanciaVertical < 15 && colisionHorizontal && this.princesa.velocidadY >= 0) {
                        this.princesa.y = isla.arriba - this.princesa.alto / 2;
                        this.princesa.velocidadY = 0;
                        this.princesa.salto = false;
                        this.princesa.caida = false;
                        this.princesa.actualColis();
                        enIsla = true;
                        break;
                    }

                    distanciaVertical = Math.abs(this.princesa.arriba - isla.abajo);
                    if (distanciaVertical < 15 && colisionHorizontal && this.princesa.velocidadY < 0) {
                        this.princesa.y = isla.abajo + this.princesa.alto / 2;
                        this.princesa.velocidadY = 0;
                        this.princesa.salto = false;
                        this.princesa.ciclos = 0;
                        this.princesa.actualColis();
                    }
                }
            }
            if (enIsla) break;
        }

        if (!enIsla) {
            this.princesa.caida = true;
        }

        // ENEMIGOS tipo 1
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null && enemigos[i].activo) {
                enemigos[i].mover();
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
                    int lado = (int) (Math.random() * 2);
                    double x, vel;
                    double[] yRandom = { 70, 250, 420 };
                    double yEnemigo = -1;
                    for (int intento = 0; intento < 10; intento++) {
                        double yCandidata = yRandom[(int) (Math.random() * 3)] + (Math.random() * 70);
                        if (!hayColisionAlSpawn(yCandidata, 80)) {
                            yEnemigo = yCandidata;
                            break;
                        }
                    }
                    if (yEnemigo == -1) break;
                    if (lado == 0) {
                        x = -30;
                        vel = 1 + Math.random() * 1.5;
                    } else {
                        x = entorno.ancho() + 30;
                        vel = -1 - Math.random() * 1.5;
                    }
                    enemigos[posicionLibre] = new Enemigo(x, yEnemigo, vel, entorno.ancho(), entorno.alto());
                }
            }
        }

        // ENEMIGOS tipo 2
        for (int i = 0; i < enemigos2.length; i++) {
            if (enemigos2[i] != null && enemigos2[i].activo) {
                enemigos2[i].mover();
                if (enemigos2[i].estaFueraDePantalla()) {
                    enemigos2[i] = null;
                }
            }
        }

        int enemigos2Activos = 0;
        for (Enemigo2 e2 : enemigos2) {
            if (e2 != null && e2.activo) enemigos2Activos++;
        }

        if (enemigos2Activos < 2) {
            int posLibre2 = -1;
            for (int i = 0; i < enemigos2.length; i++) {
                if (enemigos2[i] == null) {
                    posLibre2 = i;
                    break;
                }
            }
            if (posLibre2 != -1) {
                int lado2 = (int) (Math.random() * 2);
                double x2, vel2;
                double[] yRandom2 = { 70, 250, 420 };
                double yEnemigo2 = -1;
                for (int intento = 0; intento < 10; intento++) {
                    double yCandidata2 = yRandom2[(int) (Math.random() * 3)] + (Math.random() * 70);
                    if (!hayColisionAlSpawn(yCandidata2, 120)) {
                        yEnemigo2 = yCandidata2;
                        break;
                    }
                }
                if (yEnemigo2 == -1) yEnemigo2 = 70 + Math.random() * 350;
                if (lado2 == 0) {
                    x2 = -30;
                    vel2 = 0.6 + Math.random() * 0.7;
                } else {
                    x2 = entorno.ancho() + 30;
                    vel2 = -0.6 - Math.random() * 0.7;
                }
                enemigos2[posLibre2] = new Enemigo2(x2, yEnemigo2, vel2, entorno.ancho(), entorno.alto());
            }
        }

        // DISPARO
        if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO) && proyectil == null && !juegoTerminado) {
            int mouseX = this.entorno.mouseX();
            int mouseY = this.entorno.mouseY();
            double dx = mouseX - this.princesa.x;
            double dy = mouseY - this.princesa.y;
            double distancia = Math.sqrt(dx * dx + dy * dy);
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

        // EXPLOSIÓN
        if (this.entorno.sePresionoBoton(this.entorno.BOTON_DERECHO)
                && this.explosion == null && !juegoTerminado && this.tiempoExplosion <= 0) {
            this.explosion = new ExplosionPrincesa(this.princesa.x, this.princesa.y, entorno);
            this.tiempoExplosion = 5;
        }

        if (this.tiempoExplosion > 0 && this.entorno.numeroDeTick() % 50 == 0) {
            this.tiempoExplosion -= 1;
        }

        if (this.explosion != null) {
            this.explosion.mover(this.princesa.x, this.princesa.y);
            for (int i = 0; i < enemigos.length; i++) {
                Enemigo e = enemigos[i];
                if (e != null && e.activo) {
                    if (!(this.explosion.abajo <= e.arriba || this.explosion.arriba >= e.abajo ||
                            this.explosion.derecha <= e.izquierda || this.explosion.izquierda >= e.derecha)) {
                        enemigos[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(this.princesa.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        break;
                    }
                }
            }
            for (int i = 0; i < enemigos2.length; i++) {
                Enemigo2 e2 = enemigos2[i];
                if (e2 != null && e2.activo) {
                    if (!(this.explosion.abajo <= e2.arriba || this.explosion.arriba >= e2.abajo ||
                            this.explosion.derecha <= e2.izquierda || this.explosion.izquierda >= e2.derecha)) {
                        enemigos2[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(this.princesa.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        break;
                    }
                }
            }
            if (this.explosion.fin) {
                this.explosion = null;
            }
        }

        // COLISIONES
        for (int i = 0; i < enemigos.length; i++) {
            Enemigo e = enemigos[i];
            if (e != null && e.activo) {
                if (!(princesa.abajo <= e.arriba || princesa.arriba >= e.abajo ||
                        princesa.derecha <= e.izquierda || princesa.izquierda >= e.derecha)) {
                    vidas--;
                    enemigos[i] = null;
                    mensajeVidas = "¡PERDISTE UNA VIDA!";
                    actualizarCorazones();
                    iniciarReaparicion();
                    break;
                }
            }
        }

        for (int i = 0; i < enemigos2.length; i++) {
            Enemigo2 e2 = enemigos2[i];
            if (e2 != null && e2.activo) {
                if (!(princesa.abajo <= e2.arriba || princesa.arriba >= e2.abajo ||
                        princesa.derecha <= e2.izquierda || princesa.izquierda >= e2.derecha)) {
                    vidas -= e2.vidasQueQuita();
                    if (vidas < 0) vidas = 0;
                    enemigos2[i] = null;
                    mensajeVidas = "¡PERDISTE DOS VIDAS!";
                    actualizarCorazones();
                    iniciarReaparicion();
                    break;
                }
            }
        }

        if (proyectil != null && proyectil.activo) {
            for (int i = 0; i < enemigos.length; i++) {
                Enemigo e = enemigos[i];
                if (e != null && e.activo) {
                    if (!(proyectil.abajo <= e.arriba || proyectil.arriba >= e.abajo ||
                            proyectil.derecha <= e.izquierda || proyectil.izquierda >= e.derecha)) {
                        proyectil = null;
                        enemigos[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(this.princesa.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        break;
                    }
                }
            }
        }

        if (proyectil != null && proyectil.activo) {
            for (int i = 0; i < enemigos2.length; i++) {
                Enemigo2 e2 = enemigos2[i];
                if (e2 != null && e2.activo) {
                    if (!(proyectil.abajo <= e2.arriba || proyectil.arriba >= e2.abajo ||
                            proyectil.derecha <= e2.izquierda || proyectil.izquierda >= e2.derecha)) {
                        proyectil = null;
                        enemigos2[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(this.princesa.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        break;
                    }
                }
            }
        }

        // POCIÓN
        for (int i = 0; i < pociones.length; i++) {
            PocionVida p = pociones[i];
            if (p != null && p.activo) {
                if (!(princesa.abajo <= p.arriba || princesa.arriba >= p.abajo ||
                        princesa.derecha <= p.izquierda || princesa.izquierda >= p.derecha)) {
                    vidas++;
                    if (vidas > 6) vidas = 6;
                    actualizarCorazones();
                    pociones[i] = null;
                }
            }
        }

        // CAÍDA AL VACÍO
        if (this.princesa.y > this.entorno.alto() + 100) {
            vidas--;
            actualizarCorazones();
            iniciarReaparicion();
        }

        // VICTORIA
        if (castillo != null && castillo.activo) {
            if (!(princesa.abajo <= castillo.arriba || princesa.arriba >= castillo.abajo ||
                    princesa.derecha <= castillo.izquierda || princesa.izquierda >= castillo.derecha)) {
                this.victoria = true;
                this.juegoTerminado = true;
            }
        }

        // DERROTA
        if (this.vidas <= 0) {
            this.juegoTerminado = true;
            this.victoria = false;
        }

        dibujarTodo();
    }

    public void iniciarPartida() {
        this.vidas = 6;
        this.juegoTerminado = false;
        this.victoria = false;
        this.proyectil = null;
        this.explosion = null;
        this.contadorSpawn = 0;
        this.reapareciendo = false;
        this.primeraVez = true;
        this.fondo.x = this.entorno.ancho() / 2.0;
        this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150;
        this.enemigosEliminados = 0;
        this.tiempoExplosion = 0;

        for (int i = 0; i < pociones.length; i++) {
            pociones[i] = null;
        }

        for (int i = 0; i < corazones.length; i++) {
            corazones[i].activo = true;
        }

        this.princesa.resetearPosicion(this.entorno.ancho() / 2, 500);

        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = null;
        }

        for (int i = 0; i < enemigos2.length; i++) {
            enemigos2[i] = null;
        }
    }

    public void reiniciarJuego() {
        this.menuActivo = true;
        this.juegoTerminado = false;
        this.iniciarPartida();
    }

    public void dibujarMenu() {
        if (fondoMenu != null) {
            this.entorno.dibujarImagen(fondoMenu, this.entorno.ancho() / 2, this.entorno.alto() / 2, 0, 1.0);
        }

        if (titulo != null) {
            this.entorno.dibujarImagen(titulo, this.entorno.ancho() / 2, this.entorno.alto() / 2 - 100, 0, 0.6);
        }

        if (botonJugar != null) {
            double centroBotonX = botonJugarX + botonAncho/2;
            double centroBotonY = botonJugarY + botonAlto/2;
            this.entorno.dibujarImagen(botonJugar, centroBotonX, centroBotonY, 0, 0.28);
        }
    }

    public void dibujarFinJuego() {
        if (victoria && victoriaImage != null) {
            this.entorno.dibujarImagen(victoriaImage, this.entorno.ancho() / 2, this.entorno.alto() / 2, 0, 0.35);
        } else if (!victoria && gameOverImage != null) {
            this.entorno.dibujarImagen(gameOverImage, this.entorno.ancho() / 2, this.entorno.alto() / 2, 0, 0.35);
        }

        if (botonReiniciar != null) {
            double centroBotonX = botonReiniciarX + botonAncho/2;
            double centroBotonY = botonReiniciarY + botonAlto/2;
            this.entorno.dibujarImagen(botonReiniciar, centroBotonX, centroBotonY, 0, 0.28);
        }

        // Detectar click en el botón REINICIAR
        if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO)) {
            int mouseX = this.entorno.mouseX();
            int mouseY = this.entorno.mouseY();

            if (mouseX >= botonReiniciarX && mouseX <= botonReiniciarX + botonAncho &&
                mouseY >= botonReiniciarY && mouseY <= botonReiniciarY + botonAlto) {
                reiniciarJuego();
            }
        }

        if (this.entorno.sePresiono('r') || this.entorno.sePresiono('R')) {
            reiniciarJuego();
        }
    }

    public void generarPocion(double x, double y) {
        for (int i = 0; i < pociones.length; i++) {
            if (pociones[i] == null) {
                pociones[i] = new PocionVida(x, y, this.entorno);
                break;
            }
        }
    }

    public void actualizarCorazones() {
        for (int i = 0; i < corazones.length; i++) {
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

    public void dibujarTodo() {
        this.fondo.dibujar();

        for (Isla[] fila : this.islas) {
            for (Isla isla : fila) {
                if (isla != null) {
                    isla.dibujar();
                }
            }
        }

        if (castillo != null && castillo.activo) {
            castillo.dibujar(this.entorno);
        }

        for (Enemigo e : enemigos) {
            if (e != null && e.activo) {
                e.dibujar(this.entorno);
            }
        }

        for (Enemigo2 e2 : enemigos2) {
            if (e2 != null && e2.activo) {
                e2.dibujar(this.entorno);
            }
        }

        for (PocionVida p : pociones) {
            if (p != null && p.activo) {
                p.dibujar(this.entorno);
            }
        }

        if (proyectil != null) {
            proyectil.dibujar(this.entorno);
        }

        if (this.explosion != null) {
            this.explosion.dibujar(entorno);
        }

        if (!reapareciendo) {
            this.princesa.dibujar();
        }

        for (Corazon c : corazones) {
            c.dibujar(this.entorno);
        }

        if (reapareciendo) {
            this.entorno.cambiarFont("Arial", 20, Color.YELLOW);
            this.entorno.escribirTexto(mensajeVidas, this.entorno.ancho() / 2 - 100, this.entorno.alto() / 2);
        }
        this.entorno.cambiarFont("Arial", 14, Color.BLACK);
        this.entorno.escribirTexto("Tiempo para explosion: " + this.tiempoExplosion, 620, 100);
        this.entorno.cambiarFont("Arial", 14, Color.WHITE);
        this.entorno.escribirTexto("Próxima poción: " + (enemigosParaItem - enemigosEliminados) + " enemigos", 20, 100);
    }

    public void moverNivel() {
        if (princesa.x > (this.entorno.ancho() * 2 / 3)) {
            princesa.x -= this.velocidad;
            fondo.x -= this.velocidad;

            for (Isla[] fila : islas) {
                for (Isla isla : fila) {
                    if (isla != null) {
                        isla.x -= this.velocidad;
                        isla.actualColis();
                    }
                }
            }

            if (castillo != null) {
                castillo.x -= this.velocidad;
                castillo.actualizarColisiones();
            }

            for (PocionVida p : pociones) {
                if (p != null) {
                    p.x -= this.velocidad;
                    p.actualizarColisiones();
                }
            }

            for (Enemigo e : enemigos) {
                if (e != null && e.activo) {
                    e.x -= this.velocidad;
                    e.actualizarColisiones();
                }
            }

            for (Enemigo2 e2 : enemigos2) {
                if (e2 != null && e2.activo) {
                    e2.x -= this.velocidad;
                    e2.actualizarColisiones();
                }
            }
        }
    }

    public boolean hayColisionAlSpawn(double yCandidato, double separacionMinima) {
        for (Enemigo e : enemigos) {
            if (e != null && e.activo) {
                if (Math.abs(e.y - yCandidato) < separacionMinima)
                    return true;
            }
        }
        for (Enemigo2 e2 : enemigos2) {
            if (e2 != null && e2.activo) {
                if (Math.abs(e2.y - yCandidato) < separacionMinima)
                    return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}