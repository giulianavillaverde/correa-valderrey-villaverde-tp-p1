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

    // variables del estado
    int vidas;
    boolean juegoTerminado;
    boolean victoria;
    boolean menuActivo;
    double anchoTotalMapa;

    // variables de control de spawn (enemigos)
    int contadorSpawn;
    int intervaloMinEnemigos;
    int minEnemigosPantalla;

    // variables de control de spawn (reaparecer jugador)
    int tiempoReaparecer;
    boolean reapareciendo;
    boolean primeraVez;

    // variables ataque y recompensas
    int tiempoExplosion;
    String mensajeVidas;
    int enemigosEliminados;
    int enemigosParaItem;

    // Posiciones de los botones
    double botonJugarX, botonJugarY;
    double botonReiniciarX, botonReiniciarY;
    double botonAncho, botonAlto;

    // Jefe Final
    JefeFinal jefe;
    ProyectilJefe[] proyectilesJefe;
    boolean enPeleaJefe;

    Juego() {
        this.entorno = new Entorno(this, "Super Elizabeth Sis", 800, 600);

        // incializacion de variables
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

        // Inicializar jefe
        this.proyectilesJefe = new ProyectilJefe[30];
        this.enPeleaJefe = false;
        this.jefe = null;

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
        // MENÚ DE INICIO
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

        // PANTALLA DE FIN DE JUEGO
        if (juegoTerminado) {
            dibujarFinJuego();
            return;
        }

        // inicializacion de ubicacion de la princesa
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
                this.princesa.iniciarSalto();
            }
        }

        // Gravedad
        this.princesa.movVertical();

        // Colisión de la princesa con islas
        colisionPrincesa(this.princesa, this.islas);

        // ENEMIGOS tipo 1
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null && enemigos[i].activo) {
                enemigos[i].mover();

                for (Isla[] fila : this.islas) {
                    for (Isla isla : fila) {
                        if (isla != null) {
                            if (!(enemigos[i].abajo <= isla.arriba || enemigos[i].arriba >= isla.abajo ||
                                    enemigos[i].derecha <= isla.izquierda || enemigos[i].izquierda >= isla.derecha)) {
                                enemigos[i].velocidad *= -1;
                                enemigos[i].mover();
                            }
                        }
                    }
                }

                if (enemigos[i].estaFueraDePantalla()) {
                    enemigos[i] = null;
                }
            }
        }

        int enemigosActivos = 0;
        for (Enemigo e : enemigos) {
            if (e != null && e.activo)
                enemigosActivos++;
        }

        if (enemigosActivos < minEnemigosPantalla && !enPeleaJefe) {
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
                    if (lado == 0) {
                        x = -30;
                        vel = 1 + Math.random() * 1.5;
                    } else {
                        x = entorno.ancho() + 30;
                        vel = -1 - Math.random() * 1.5;
                    }

                    double[] yRandom = { 50, 230, 450 };
                    double yEnemigo = -1;
                    for (int intento = 0; intento < 20; intento++) {
                        double yCandidata = yRandom[(int) (Math.random() * 3)] + (Math.random() * 20 - 10);
                        if (!hayColisionAlSpawn(x, yCandidata, 80)) {
                            yEnemigo = yCandidata;
                            break;
                        }
                    }
                    if (yEnemigo == -1)
                        break;

                    enemigos[posicionLibre] = new Enemigo(x, yEnemigo, vel, entorno.ancho(), entorno.alto());
                }
            }
        }

        // ENEMIGOS tipo 2
        for (int i = 0; i < enemigos2.length; i++) {
            if (enemigos2[i] != null && enemigos2[i].activo) {
                enemigos2[i].mover();

                for (Isla[] fila : this.islas) {
                    for (Isla isla : fila) {
                        if (isla != null) {
                            if (!(enemigos2[i].abajo <= isla.arriba || enemigos2[i].arriba >= isla.abajo ||
                                    enemigos2[i].derecha <= isla.izquierda || enemigos2[i].izquierda >= isla.derecha)) {
                                enemigos2[i].velocidad *= -1;
                                enemigos2[i].mover();
                            }
                        }
                    }
                }

                if (enemigos2[i].estaFueraDePantalla()) {
                    enemigos2[i] = null;
                }
            }
        }

        int enemigos2Activos = 0;
        for (Enemigo2 e2 : enemigos2) {
            if (e2 != null && e2.activo)
                enemigos2Activos++;
        }

        if (enemigos2Activos < 2 && !enPeleaJefe) {
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
                if (lado2 == 0) {
                    x2 = -30;
                    vel2 = 0.6 + Math.random() * 0.7;
                } else {
                    x2 = entorno.ancho() + 30;
                    vel2 = -0.6 - Math.random() * 0.7;
                }

                double[] yRandom2 = { 50, 230, 450 };
                double yEnemigo2 = -1;
                for (int intento = 0; intento < 20; intento++) {
                    double yCandidata2 = yRandom2[(int) (Math.random() * 3)] + (Math.random() * 20 - 10);
                    if (!hayColisionAlSpawn(x2, yCandidata2, 80)) {
                        yEnemigo2 = yCandidata2;
                        break;
                    }
                }
                if (yEnemigo2 != -1) {
                    enemigos2[posLibre2] = new Enemigo2(x2, yEnemigo2, vel2, entorno.ancho(), entorno.alto());
                }
            }
        }

        // DISPARO (también durante la pelea con el jefe)
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
            colisionExplosio(this.explosion, this.enemigos, this.enemigos2, this.princesa);
            if (this.explosion.fin) {
                this.explosion = null;
            }
        }

        // evaluacion de daños recibidos (colisiones con enemigos)
        colisionPrincesa(this.princesa, this.enemigos);
        
        colisionPrincesa(this.princesa, this.enemigos2);
        

        // colisiones de los proyectiles contra los enemigos tipo 1
        if (colisionProyectil(this.proyectil, this.enemigos, this.enemigos2, this.princesa)) {
        	this.proyectil = null;
        }

        // recoleccion de las pociones de vida
        for (int i = 0; i < pociones.length; i++) {
            PocionVida p = pociones[i];
            if (p != null && p.activo) {
                if (!(princesa.abajo <= p.arriba || princesa.arriba >= p.abajo ||
                        princesa.derecha <= p.izquierda || princesa.izquierda >= p.derecha)) {
                    vidas++;
                    if (vidas > 6)
                        vidas = 6;
                    actualizarCorazones();
                    pociones[i] = null;
                }
            }
        }

        // CAÍDA AL VACÍO
        if (this.princesa.y > this.entorno.alto() + 100) {
            vidas--;
            mensajeVidas = "¡PERDISTE UNA VIDA!";
            actualizarCorazones();
            iniciarReaparicion();
        }

        // ==================== PELEA CON EL JEFE FINAL ====================
        // Activar pelea al tocar el castillo
        if (!this.enPeleaJefe && this.castillo != null && this.castillo.activo) {
            if (!(this.princesa.abajo <= this.castillo.arriba || this.princesa.arriba >= this.castillo.abajo ||
            		this.princesa.derecha <= this.castillo.izquierda || this.princesa.izquierda >= this.castillo.derecha)) {
            	iniciarPeleaJefe(this.castillo, this.jefe, this.enemigos, this.enemigos2, this.fondo, this.islas);
        		double posX = this.castillo.x + 100;
                double posY = this.castillo.y;
            	this.jefe = new JefeFinal(posX, posY, this.entorno, this.vidas);
            }
        }

        // Lógica de la pelea con el jefe
        if (enPeleaJefe && jefe != null && jefe.isActivo()) {

            // Mover jefe hacia la princesa
            jefe.moverHacia(princesa.x);

            // El jefe ataca cada cierto tiempo
            if (jefe.puedeAtacar()) {
                int posLibre = -1;
                for (int i = 0; i < proyectilesJefe.length; i++) {
                    if (proyectilesJefe[i] == null) {
                        posLibre = i;
                        break;
                    }
                }
                if (posLibre != -1) {
                    proyectilesJefe[posLibre] = new ProyectilJefe(jefe.x, jefe.y, princesa.x, princesa.y,
                            entorno.ancho(), entorno.alto());
                }
            }

            // Mover proyectiles del jefe
            for (int i = 0; i < proyectilesJefe.length; i++) {
                if (proyectilesJefe[i] != null && proyectilesJefe[i].activo) {
                    proyectilesJefe[i].mover();
                    if (proyectilesJefe[i].estaFueraDePantalla()) {
                        proyectilesJefe[i] = null;
                    }
                }
            }

            // Colisión proyectil del jefe - princesa
            colisionPrincesa(this.princesa, this.proyectilesJefe);
            

            // Colisión proyectil de la princesa - jefe
            if (colisionProyectil(this.proyectil, this.jefe)) {
            	this.proyectil = null;
            }
            

            // Si el jefe muere, ganar el juego
            if (!jefe.isActivo()) {
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

	// Metodo Colisión de la princesa con islas
    public void colisionPrincesa(Princesa p, Isla[][] i) {
    	boolean enIsla = false;
		for (Isla[] fila : i) {
            for (Isla isla : fila) {
                if (isla != null) {
                    boolean colisionHorizontal = p.derecha > isla.izquierda
                            && p.izquierda < isla.derecha;

                    double distanciaVertical = Math.abs(p.abajo - isla.arriba);

                    if (distanciaVertical < 15 && colisionHorizontal && p.velocidadY >= 0) {
                        p.y = isla.arriba - p.alto / 2;
                        p.velocidadY = 0;
                        p.salto = false;
                        p.caida = false;
                        p.actualColis();
                        enIsla = true;
                        break;
                    }

                    distanciaVertical = Math.abs(p.arriba - isla.abajo);
                    if (distanciaVertical < 15 && colisionHorizontal && p.velocidadY < 0) {
                        p.y = isla.abajo + p.alto / 2;
                        p.velocidadY = 0;
                        p.salto = false;
                        p.ciclos = 0;
                        p.actualColis();
                    }
                }
            }
            if (enIsla)
                break;
        }

        if (!enIsla) {
            p.caida = true;
        }
    }
    
    // Metodo colisiones con enemigos (evaluacion de daños recibidos)
    public void colisionPrincesa(Princesa p, Enemigo[] e) {
    	for (int i = 0; i < e.length; i++) {
            if (e[i] != null && e[i].activo) {
                if (!(p.abajo <= e[i].arriba || p.arriba >= e[i].abajo ||
                        p.derecha <= e[i].izquierda || p.izquierda >= e[i].derecha)) {
                    vidas--;
                    e[i] = null;
                    mensajeVidas = "¡PERDISTE UNA VIDA!";
                    actualizarCorazones();
                    iniciarReaparicion();
                    return;
                }
            }
        }
    }
    
    public void colisionPrincesa(Princesa p, Enemigo2[] e) {
    	for (int i = 0; i < e.length; i++) {
            if (e[i] != null && e[i].activo) {
                if (!(p.abajo <= e[i].arriba || p.arriba >= e[i].abajo ||
                        p.derecha <= e[i].izquierda || p.izquierda >= e[i].derecha)) {
                    vidas -= e[i].vidasQueQuita();
                    if (vidas < 0)
                        vidas = 0;
                    enemigos2[i] = null;
                    mensajeVidas = "¡PERDISTE DOS VIDAS!";
                    actualizarCorazones();
                    iniciarReaparicion();
                    return;
                }
            }
        }
    }
    
    // Metodo Colisión proyectil del jefe - princesa
    public void colisionPrincesa(Princesa p, ProyectilJefe[] pj) {
    	for (int i = 0; i < pj.length; i++) {
            if (pj[i] != null && pj[i].activo) {
                if (!(p.abajo <= pj[i].arriba || p.arriba >= pj[i].abajo ||
                        p.derecha <= pj[i].izquierda || p.izquierda >= pj[i].derecha)) {
                    pj[i] = null;
                    vidas--;
                    mensajeVidas = "¡PERDISTE DOS VIDAS!";
                    actualizarCorazones();
                    iniciarReaparicion();
                    if (vidas <= 0) {
                        this.juegoTerminado = true;
                        this.victoria = false;
                    }
                    break;
                }
            }
        }
    }
    
    // colisiones de los proyectiles contra los enemigos
    public boolean colisionProyectil(ProyectilPrincesa p, Enemigo[] e, Enemigo2[] e2, Princesa pr) {
    	if (p != null && p.activo) {
            for (int i = 0; i < e.length; i++) {
                if (e[i] != null && e[i].activo) {
                    if (!(p.abajo <= e[i].arriba || p.arriba >= e[i].abajo ||
                            p.derecha <= e[i].izquierda || p.izquierda >= e[i].derecha)) {
                        e[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(pr.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        return true;
                    }
                }
            }
            for (int i = 0; i < e2.length; i++) {
                if (e2[i] != null && e2[i] .activo) {
					if (!(p.abajo <= e2[i] .arriba || p.arriba >= e2[i] .abajo ||
                            p.derecha <= e2[i].izquierda || p.izquierda >= e2[i].derecha)) {
                        e2[i] = null;
                        enemigosEliminados++;
                        if (enemigosEliminados >= enemigosParaItem) {
                            generarPocion(this.princesa.x + 100, this.princesa.y - 50);
                            enemigosEliminados = 0;
                        }
                        return true;
                    }
                }
            }
        }
    	return false;
    }
    
    // Metodo Colisión proyectil del jefe - princesa
    public boolean colisionProyectil(ProyectilPrincesa p, JefeFinal j) {
		if (p != null && p.activo) {
			if (!(p.abajo <= j.arriba || p.arriba >= j.abajo ||
                    p.derecha <= j.izquierda || p.izquierda >= j.derecha)) {
                j.recibirDanio();
                return true;
            }
        }
		return false;
    }
    
    public void colisionExplosio(ExplosionPrincesa ex, Enemigo[] e, Enemigo2[] e2, Princesa p) {
		for (int i = 0; i < e.length; i++) {
            if (e[i] != null && e[i].activo) {
                if (!(ex.abajo <= e[i].arriba || ex.arriba >= e[i].abajo ||
                        ex.derecha <= e[i].izquierda || ex.izquierda >= e[i].derecha)) {
                    e[i] = null;
                    enemigosEliminados++;
                    if (enemigosEliminados >= enemigosParaItem) {
                        generarPocion(p.x + 100, p.y - 50);
                        enemigosEliminados = 0;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < enemigos2.length; i++) {
            if (e2[i] != null && e2[i].activo) {
                if (!(ex.abajo <= e2[i].arriba || ex.arriba >= e2[i].abajo ||
                        ex.derecha <= e2[i].izquierda || ex.izquierda >= e2[i].derecha)) {
                    e2[i] = null;
                    enemigosEliminados++;
                    if (enemigosEliminados >= enemigosParaItem) {
                        generarPocion(p.x + 100, p.y - 50);
                        enemigosEliminados = 0;
                    }
                    return;
                }
            }
        }
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
        this.enPeleaJefe = false;
        this.jefe = null;
        for (int i = 0; i < proyectilesJefe.length; i++) {
            proyectilesJefe[i] = null;
        }

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
        this.vidas = 6;
        this.juegoTerminado = false;
        this.victoria = false;
        this.proyectil = null;
        this.explosion = null;
        this.contadorSpawn = 0;
        this.reapareciendo = false;
        this.primeraVez = true;
        this.menuActivo = false;
        this.fondo.x = this.entorno.ancho() / 2.0;
        this.fondo.x += this.fondo.imagenFondo.getHeight(null) - 150;
        this.fondo.escala = 1.5;
        this.fondo.imagenFondo = Herramientas.cargarImagen("juego/fondonuevo.png");
        this.enemigosEliminados = 0;
        this.tiempoExplosion = 0;
        this.enPeleaJefe = false;
        this.jefe = null;
        for (int i = 0; i < proyectilesJefe.length; i++) {
            proyectilesJefe[i] = null;
        }

        for (int i = 0; i < pociones.length; i++) {
            pociones[i] = null;
        }

        for (int i = 0; i < corazones.length; i++) {
            corazones[i].activo = true;
        }

        this.islas = new Isla[3][15];

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
        
        for(Isla[] fila: islas) for(Isla isla: fila) {
    		if(isla != null && isla.tipo == 1) {
    			isla.imagen = Herramientas.cargarImagen("juego/islaGrande.png");
    		} else if (isla != null && isla.tipo == 2) {
    			isla.imagen = Herramientas.cargarImagen("juego/islaMediana.png");
    		} else if (isla != null){
    			isla.imagen = Herramientas.cargarImagen("juego/islaChica.png");
    		}
    		
    	}

        double posicionCastillo = ultimaXMediana;
        if (posicionCastillo > anchoTotalMapa - 150) {
            posicionCastillo = anchoTotalMapa - 150;
        }
        this.castillo = new Castillo(posicionCastillo, 250, this.entorno);

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

        for (int i = 0; i < enemigos.length; i++) {
            enemigos[i] = null;
        }

        for (int i = 0; i < enemigos2.length; i++) {
            enemigos2[i] = null;
        }
    }

    public void dibujarMenu() {
        if (fondoMenu != null) {
            this.entorno.dibujarImagen(fondoMenu, this.entorno.ancho() / 2, this.entorno.alto() / 2, 0, 1.0);
        }

        if (titulo != null) {
            this.entorno.dibujarImagen(titulo, this.entorno.ancho() / 2, this.entorno.alto() / 2 - 100, 0, 0.6);
        }

        if (botonJugar != null) {
            double centroBotonX = botonJugarX + botonAncho / 2;
            double centroBotonY = botonJugarY + botonAlto / 2;
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
            double centroBotonX = botonReiniciarX + botonAncho / 2;
            double centroBotonY = botonReiniciarY + botonAlto / 2;
            this.entorno.dibujarImagen(botonReiniciar, centroBotonX, centroBotonY, 0, 0.28);
        }

        if (this.entorno.sePresionoBoton(this.entorno.BOTON_IZQUIERDO)) {
            int mouseX = this.entorno.mouseX();
            int mouseY = this.entorno.mouseY();

            if (mouseX >= botonReiniciarX && mouseX <= botonReiniciarX + botonAncho &&
                    mouseY >= botonReiniciarY && mouseY <= botonReiniciarY + botonAlto) {
                reiniciarJuego();
                return;
            }
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

        // Dibujar jefe si está en pelea
        if (enPeleaJefe && jefe != null && jefe.isActivo()) {
            jefe.dibujar(this.entorno);
            jefe.dibujarCorazones(this.entorno);

            for (ProyectilJefe pj : proyectilesJefe) {
                if (pj != null && pj.activo) {
                    pj.dibujar(this.entorno);
                }
            }
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

            // Mover jefe y sus proyectiles si está en pelea
            if (enPeleaJefe && jefe != null) {
                jefe.x -= this.velocidad;
                jefe.actualizarColisiones();
                for (ProyectilJefe pj : proyectilesJefe) {
                    if (pj != null) {
                        pj.x -= this.velocidad;
                        pj.actualizarColisiones();
                    }
                }
            }
        }
    }
    
    // Metodo que cambia fondo islas y quita enemigos para el boss
    private void iniciarPeleaJefe(Castillo c, JefeFinal j, Enemigo[] e, Enemigo2[] e2, Fondo f, Isla[][] i) {
    	enPeleaJefe = true;
        c.activo = false;
        for(Enemigo en: e) {
    		if (en != null) en.activo = false;
    	}
    	for(Enemigo2 en: e2) {
    		if (en != null) en.activo = false;
    	}
    	for(Isla[] fila: i) for(Isla isla: fila) {
    		if(isla != null && isla.tipo == 1) {
    			isla.imagen = Herramientas.cargarImagen("juego/islaGrandeCastillo.png");
    		} else if (isla != null && isla.tipo == 2) {
    			isla.imagen = Herramientas.cargarImagen("juego/islaMedianaCastillo.png");
    		} else if (isla != null){
    			isla.imagen = Herramientas.cargarImagen("juego/islaChicaCastillo.png");
    		}
    		
    	}
    	mensajeVidas = "¡BATALLA FINAL!";
    	iniciarReaparicion();
		f.imagenFondo = Herramientas.cargarImagen("juego/fondoCastillo.png");
		f.escala = 2.1;
		f.y = this.entorno.alto() / 2;
		f.x = this.entorno.ancho() / 2 + 150;
	}
    
    public boolean hayColisionAlSpawn(double xCandidato, double yCandidato, double separacionMinima) {
        for (Enemigo e : enemigos) {
            if (e != null && e.activo) {
                if (Math.abs(e.y - yCandidato) < separacionMinima && Math.abs(e.x - xCandidato) < separacionMinima)
                    return true;
            }
        }
        for (Enemigo2 e2 : enemigos2) {
            if (e2 != null && e2.activo) {
                if (Math.abs(e2.y - yCandidato) < separacionMinima && Math.abs(e2.x - xCandidato) < separacionMinima)
                    return true;
            }
        }

        for (Isla[] fila : islas) {
            for (Isla isla : fila) {
                if (isla != null) {
                    if (Math.abs(isla.y - yCandidato) < 55) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Juego juego = new Juego();
    }
}