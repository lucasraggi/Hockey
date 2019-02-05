import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import javafx.geometry.Point2D;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static com.jogamp.opengl.GL2GL3.*;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.*;

public class BasicLine extends GLCanvas implements GLEventListener {

    private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 780; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second

    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the OpenGL rendering canvas
                GLCanvas canvas = new BasicLine();
                canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
                frame.getContentPane().add(canvas);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Use a dedicate thread to run the stop() to ensure that the
                        // animator stops before program exits.
                        new Thread() {
                            @Override
                            public void run() {
                                if (animator.isStarted()) animator.stop();
                                System.exit(0);
                            }
                        }.start();
                    }
                });
                frame.setTitle(TITLE);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    private GLU glu;


    public BasicLine() {
        this.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        glu.gluOrtho2D(0, CANVAS_WIDTH, 0, CANVAS_HEIGHT);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    private void drawLine(int x1, int y1, int x2, int y2, GL2 gl) {
        double m = (y2 -y1)/((x2 - x1) *1.0);
        int x, y;

        gl.glBegin(GL_POINTS);
        for (x = x1; x<x2 ; x++) {
            y = (int) (y1 + m*(x-x1));
            gl.glVertex2i(x, y);
        }
        gl.glEnd();
    }

    private void drawLineBresenham(int x1, int y1, int x2, int y2, GL2 gl) {
        // delta of exact value and rounded value of the dependent variable
        int d = 0;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int dx2 = 2 * dx; // slope scaling factors to
        int dy2 = 2 * dy; // avoid floating point

        int ix = x1 < x2 ? 1 : -1; // increment direction
        int iy = y1 < y2 ? 1 : -1;

        int x = x1;
        int y = y1;
        gl.glBegin(GL_POINTS);
        if (dx >= dy) {
            while (true) {
                gl.glVertex2d(x, y);
                if (x == x2)
                    break;
                x += ix;
                d += dy2;
                if (d > dx) {
                    y += iy;
                    d -= dx2;
                }
            }
        } else {
            while (true) {
                gl.glVertex2d(x, y);
                if (y == y2)
                    break;
                y += iy;
                d += dx2;
                if (d > dy) {
                    x += ix;
                    d -= dy2;
                }
            }
        }
        gl.glEnd();
    }

    private void drawCircle(int xc, int yc, int x, int y, GL2 gl, int quadrant, boolean part) {
        gl.glBegin(GL_POINTS);
        if (part){
            if (quadrant == 1) {
                gl.glVertex2d(xc + x, yc + y);
                gl.glVertex2d(xc + y, yc + x);

            } else if (quadrant == 2) {
                gl.glVertex2d(xc - x, yc + y);
                gl.glVertex2d(xc - y, yc + x);

            } else if (quadrant == 3) {
                gl.glVertex2d(xc - x, yc - y);
                gl.glVertex2d(xc - y, yc - x);

            } else if (quadrant == 4) {
                gl.glVertex2d(xc + y, yc - x);
                gl.glVertex2d(xc + x, yc - y);
            }
        } else {

            gl.glVertex2d(xc + x, yc + y);
            gl.glVertex2d(xc + y, yc + x);
            gl.glVertex2d(xc - x, yc + y);
            gl.glVertex2d(xc - y, yc + x);
            gl.glVertex2d(xc - x, yc - y);
            gl.glVertex2d(xc - y, yc - x);
            gl.glVertex2d(xc + y, yc - x);
            gl.glVertex2d(xc + x, yc - y);

        }
        gl.glEnd();
    }

    private void circleBres(int xc, int yc, int r, GL2 gl, int quadrant, boolean part) {
        int x = 0, y = r;
        int d = 3 - 2 * r;
        drawCircle(xc, yc, x, y, gl, quadrant, part);

        while (y >= x) {
            x++;

            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            }
            else
                d = d + 4 * x + 6;

            drawCircle(xc, yc, x, y, gl, quadrant, part);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        /*
        *  x1 = 100
        *  x2 = 450
        *
        *  y1 = 50
        *  y2 = 750
        *
        * */

        // Edges
        drawLineBresenham(100, 100, 100, 700, gl);
        drawLineBresenham(450, 100, 450, 700, gl);
        drawLineBresenham(150, 750, 400, 750, gl);
        drawLineBresenham(150, 50, 400, 50, gl);

        // Center
        drawLineBresenham(100, 400, 450, 400, gl );
        circleBres(275, 400, 50, gl, 0, false);

        drawLineBresenham(100, 475,450, 475, gl );
        circleBres(200, 460, 1, gl, 0, false);
        circleBres(350, 460, 1, gl, 0, false);

        drawLineBresenham(100, 325,450, 325, gl );
        circleBres(200, 340, 1, gl, 0, false);
        circleBres(350, 340, 1, gl, 0, false);

        // Top
        drawLineBresenham(100, 675,450, 675, gl );
        circleBres(275, 675, 15, gl, 3, true);
        circleBres(275, 675, 15, gl, 4, true);

        circleBres(200, 600, 45, gl, 0, false);
        circleBres(200, 600, 1, gl, 0, false);
        drawLineBresenham(145, 607,155, 607, gl );
        drawLineBresenham(145, 593,155, 593, gl );
        drawLineBresenham(245, 607,255, 607, gl );
        drawLineBresenham(245, 593,255, 593, gl );

        circleBres(350, 600, 45, gl, 0, false);
        circleBres(350, 600, 1, gl, 0, false);
        drawLineBresenham(295, 607,305, 607, gl );
        drawLineBresenham(295, 593,305, 593, gl );
        drawLineBresenham(395, 607,405, 607, gl );
        drawLineBresenham(395, 593,405, 593, gl );

        // Down
        drawLineBresenham(100, 125,450, 125, gl );
        circleBres(275, 125, 15, gl, 1, true);
        circleBres(275, 125, 15, gl, 2, true);

        circleBres(200, 215, 45, gl, 0, false);
        circleBres(200, 215, 1, gl, 0, false);
        drawLineBresenham(145, 222,155, 222, gl );
        drawLineBresenham(145, 208,155, 208, gl );
        drawLineBresenham(245, 222,255, 222, gl );
        drawLineBresenham(245, 208,255, 208, gl );

        circleBres(350, 215, 45, gl, 0, false);
        circleBres(350, 215, 1, gl, 0, false);
        drawLineBresenham(295, 222,305, 222, gl );
        drawLineBresenham(295, 208,305, 208, gl );
        drawLineBresenham(395, 222,405, 222, gl );
        drawLineBresenham(395, 208,405, 208, gl );

        // Rounded edges
        circleBres(400, 700, 50, gl, 1, true);
        circleBres(150, 700, 50, gl, 2, true);
        circleBres(150, 100, 50, gl, 3, true);
        circleBres(400, 100, 50, gl, 4, true);

    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}