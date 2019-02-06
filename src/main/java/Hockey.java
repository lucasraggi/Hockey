import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.jogamp.opengl.GL.*;

public class Hockey extends GLCanvas implements GLEventListener {

    private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
    private static final int CANVAS_WIDTH = 640;  // width of the drawable
    private static final int CANVAS_HEIGHT = 780; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    private Drawer dw = new Bresenham();
    private GLU glu;
    private float thickness = 1;

    public static void main(String[] args) {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the OpenGL rendering canvas
                GLCanvas canvas = new Hockey();
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

    public Hockey() {
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

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPointSize(this.thickness);

        /*
        *  x1 = 100
        *  x2 = 450
        *
        *  y1 = 50
        *  y2 = 750
        *
        * */

        // Edges
        dw.drawLine(100, 100, 100, 700, gl);
        dw.drawLine(450, 100, 450, 700, gl);
        dw.drawLine(150, 750, 400, 750, gl);
        dw.drawLine(150, 50, 400, 50, gl);

        // Center
        dw.drawLine(100, 400, 450, 400, gl );
        dw.drawCircle(275, 400, 50, gl, 0, false);

        dw.drawLine(100, 475,450, 475, gl );
        dw.drawCircle(200, 460, 1, gl, 0, false);
        dw.drawCircle(350, 460, 1, gl, 0, false);

        dw.drawLine(100, 325,450, 325, gl );
        dw.drawCircle(200, 340, 1, gl, 0, false);
        dw.drawCircle(350, 340, 1, gl, 0, false);

        // Top
        dw.drawLine(100, 675,450, 675, gl );
        dw.drawCircle(275, 675, 15, gl, 3, true);
        dw.drawCircle(275, 675, 15, gl, 4, true);

        dw.drawCircle(200, 600, 45, gl, 0, false);
        dw.drawCircle(200, 600, 1, gl, 0, false);
        dw.drawLine(145, 607,155, 607, gl );
        dw.drawLine(145, 593,155, 593, gl );
        dw.drawLine(245, 607,255, 607, gl );
        dw.drawLine(245, 593,255, 593, gl );

        dw.drawCircle(350, 600, 45, gl, 0, false);
        dw.drawCircle(350, 600, 1, gl, 0, false);
        dw.drawLine(295, 607,305, 607, gl );
        dw.drawLine(295, 593,305, 593, gl );
        dw.drawLine(395, 607,405, 607, gl );
        dw.drawLine(395, 593,405, 593, gl );

        // Down
        dw.drawLine(100, 125,450, 125, gl );
        dw.drawCircle(275, 125, 15, gl, 1, true);
        dw.drawCircle(275, 125, 15, gl, 2, true);

        dw.drawCircle(200, 215, 45, gl, 0, false);
        dw.drawCircle(200, 215, 1, gl, 0, false);
        dw.drawLine(145, 222,155, 222, gl );
        dw.drawLine(145, 208,155, 208, gl );
        dw.drawLine(245, 222,255, 222, gl );
        dw.drawLine(245, 208,255, 208, gl );

        dw.drawCircle(350, 215, 45, gl, 0, false);
        dw.drawCircle(350, 215, 1, gl, 0, false);
        dw.drawLine(295, 222,305, 222, gl );
        dw.drawLine(295, 208,305, 208, gl );
        dw.drawLine(395, 222,405, 222, gl );
        dw.drawLine(395, 208,405, 208, gl );

        // Rounded edges
        dw.drawCircle(400, 700, 50, gl, 1, true);
        dw.drawCircle(150, 700, 50, gl, 2, true);
        dw.drawCircle(150, 100, 50, gl, 3, true);
        dw.drawCircle(400, 100, 50, gl, 4, true);

    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}