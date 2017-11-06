/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * COMPUTACAO GRAFICA LWJGL E OPENGL TRADICIONAL PARA DESENHO DE SEGMENTOS DE
 * RETA FORNECIDOS PELO TECLADO
 *
 * PROF. MARCELA
 * Modificado e Adaptado por:
 * ANDRÉ LEVI ZANON
 * RODRIGO FERRARI DE SOUZA
 * VICTOR HUGO DOMINGOS DE ABREU
 *
 * PARA INSTALAR A BIBLIOTECA VER TUTORIAL EM
 * http://wiki.lwjgl.org/wiki/Setting_Up_LWJGL_with_NetBeans.html
 *
 */
public class Main {

    public static final int DISPLAY_HEIGHT = 480;
    public static final int DISPLAY_WIDTH = 640;
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static final int MAX_POINTS = 1000;

    private int[] x;
    private int[] y;
    private int count;// CONTADOR DE PONTOS DO MOUSE QUE FORAM PRESSIONADOS
    boolean opened; //DETERMINA SE O POLIGOno ESTA ABERTO OU FOI FECHADO --> FINALIZOU

    //COMPONENTE DE TRATAMENTO DE ERROS
    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.toString(), ex);
        }
    }

    public static void main(String[] args) {

        Main main = null;
        try {
            System.out.println("Use p teclado para fechar o poligono:");
            System.out.println("down  - fecha o poligono");

            main = new Main();
            main.create();
            main.run();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            if (main != null) {
                main.destroy();
            }
        }
    }

    /**
     * INICIALIZACAO DO NOSSO VETOR DE PONTOS E VARIAVEIS PARA CONTROLE
     */
    public Main() {
        x = new int[MAX_POINTS];
        y = new int[MAX_POINTS];
        count = 0; //CONTA O NRO DE PONTOS CLICADOS
        opened = true; // TRUE ENQUANDO O POLIGONO ESTIVER ABERTO
    }

    public void create() throws LWJGLException {
        //Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle("Desenho de Poligonos");
        Display.create();

        //Keyboard
        Keyboard.create();

        //Mouse
        Mouse.setGrabbed(false);
        Mouse.create();

        //OpenGL
        initGL();
        resizeGL();
    }

    public void destroy() {
        //Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void initGL() {
        //2D Initialization
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);
    }

    /**
     * FUNCAO QUE PROCESSA OS EVENTOS DO TECLADO
     */
    public void processKeyboard() {
        //Square's Size
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            x[count] = x[0];
            y[count++] = y[0];
            opened = false;
        }

    }

    /*
    FUNCAO QUE COLETA AS COORDENADAS DO MOUSE AO MESMO SER PRESSIONADO 
     */
    public void processMouse() {
        if (opened && Mouse.isButtonDown(0)) {
            x[count] = Mouse.getX();
            y[count++] = Mouse.getY();
            //System.out.println("(" + Mouse.getX()+", " + Mouse.getX()+")" );
            System.out.println(x[count - 1] + "," + y[count - 1]);
        }

    }

    /*
    FUNCAO RESPOSAVEL PELO DESENHO
     */
    public void render() {
        //LIMPA A TELA COM A COR DE FUNDO DEFINIDA ANTERIORMENTE
        glClear(GL_COLOR_BUFFER_BIT);
        //ZERA A MATRIZ DE DESENHO
        glLoadIdentity();

        //DEFINE A COR DE DESENHO
        glColor3f(0.0f, 0.5f, 0.5f);

        //DESENHA AS LINHAS SEMPRE EM DUPLAS DOS PONTOS COLETADOS NOS EVENTOS DO MOUSE
        System.out.println("count=" + count);
        if (count > 1 && count < MAX_POINTS) {
            for (int i = 1; i < count;) {
                algBresenham(x[i - 1], y[i - 1], x[i], y[i]);
                //glBegin(GL_POINTS);
                //glVertex2i(x[i - 1], y[i - 1]);
                //glVertex2i(x[i], y[i]);
                glEnd();
                i++;
            }

        }
    }

    /*A TELA DE DESENHO PODE SER REPOSICIONADA E REDESENHADA
    //ESTA FUNCAO SETA O TAMANHO DA PORTA DE VISAO AONDE EH FEITO O DESENHO
    // A PROJECAO PARA 2D COMO SENDO ORTOGRAFICA (CORTANDO O VALOR DA COORDENADA Z, E A LARGURA EM X E EM Y IGUAL A DA PORTA DE VISAO, 
    //LEMBRA QUE EU FALEI EM SALA DE AULA)
    // E INICIALIZA A MATRIZ DE DESENHO MODELVIEW COMO VAZIA CARREGANDO NELA A INDENTIDADE
     */
    public void resizeGL() {
        //2D Scene
        glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0.0f, DISPLAY_WIDTH, 0.0f, DISPLAY_HEIGHT);
        glPushMatrix();

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
    }

    //OPENGL PARA FUNCIONAR EM LWJGL EH UM THREAD QUE CHAMA FUNCOES DE TRATAMENTO DO TECLADO, MOUSE, UPDATE E RENDER (REDESENHO NA TELA)
    //CONFIGURACOES DOS TREADS
    public void run() {
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            if (Display.isVisible()) {
                processKeyboard();
                processMouse();
                update();
                render(); //FUNCAO DE DESENHO GRAFICO, TODA TELA EH REDESENHADA NA FRAME RATE POR ESSA FUNCAO
            } else {
                if (Display.isDirty()) {
                    render();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }
            }
            Display.update();
            Display.sync(60); //TAXA DE RESTAURO
        }
    }

    public void update() {
        //FUCAO VAZIA, UTILIZAR QUANDO NECESSIDAR ATUALIZAR ALGUMA INFORMACAO AO FAZER A ATUALIZACAO DE ATRIBUTOS DO DESENHO
    }
    
    public static void algBresenham(int x1, int y1, int x2, int y2){

        glBegin(GL_POINTS);

        int dx = x2 - x1;
        int dy = y2 - y1;
        int inclinacao;  

        // No caso do primeiro valor de x ser maior do que o segundo o algoritmo eh chamado
        // novamente devido a simetria para inverter os valores de x1  e x2
        if(dx<0){ 
            algBresenham(x2, y2, x1, y1);
            return;
        }
        if(dy<0)
            inclinacao = -1;
        else
            inclinacao = 1;

        int d, incE, incNE;

        int x = x1;
        int y = y1;

        glVertex2i(x, y);
        if(dx >= inclinacao*dy){// m<=1 
            if(dy<0){ // caso y2<y1, primeiro octante
                d = 2*dy+dx;
                incE = 2*dy;
                incNE = 2*(dy+dx);
                while(x<x2){
                    if(d<0){ // escolhe NE
                        d += incNE;
                        x++;
                        y--; //desde em y pois y2 < y1
                    }
                    else{ // escolhe E
                        d+= incE;
                        x++; // varia apenas no eixo x
                    }
                    glVertex2i(x, y);
                }
            }
            else{ // caso y1<y2, quarto octante
                d=2*dy-dx;
                incE = 2*dy;
                incNE = 2*(dy-dx);
                while(x<x2){
                    if(d<0){ // escolhe E
                        d+= incE;
                        x++; // varia apenas no eixo x
                    }
                    else{ // escolhe NE
                        d+= incNE;
                        x++;
                        y++;
                    }
                glVertex2i(x, y);
                }
            }
        }
        else{ // |m|>1
            if(dy<0){ // caso y2<y1, segundo octante
                d=dy+2*dx;
                incE = 2*dx;
                incNE = 2*(dy+dx);
                while(y > y2){
                    if(d<0){ 
                        d += incE;
                        y--; // varia apenas no eixo y
                    }
                    else{ 
                        d+= incNE;
                        x++;
                        y--;
                    }
                    glVertex2i(x, y);
                }
            }
            else{ // caso y1<y2, terceito octante. os outros octantes sao tratados na simetria invertento
                  //o ponto 1 com o ponto 2
                d=dy-2*dx;
                incE = -2*dx;
                incNE = 2*(dy-dx);
                while(y<y2){
                    if(d<0){ 
                        d+=incNE;
                        x++;
                        y++; 
                    }
                    else{
                        d+= incE;
                        y++; // varia apenas no eixo y
                    }
                    glVertex2i(x, y);
                    }
                }
            }
        glVertex2i(x, y);
    }

}