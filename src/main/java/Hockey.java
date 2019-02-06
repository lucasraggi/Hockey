import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.*;

public class Hockey extends GLCanvas implements GLEventListener, MouseListener, MouseMotionListener, ActionListener {

    private static String TITLE = "JOGL 2.0 Setup (GLCanvas)";  // window's title
    private static final int CANVAS_WIDTH = 1300;  // width of the drawable
    private static final int CANVAS_HEIGHT = 780; // height of the drawable
    private static final int FPS = 60; // animator's target frames per second
    private static int firstClickX = -1;
    private static int firstClickY = -1;
    private static final int TICK_MIN = 0;
    private static final int TICK_INIT = 2;
    private static final int TICK_MAX = 10;
    private ArrayList<Line> lines = new ArrayList<>();
    private Drawer dw = new Bresenham();
    private GLU glu;

    private static float thickness = 1;
    private static int algorithimSelected = 1;
    private static float red = 1, green = 1, blue = 1;




    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        y = Math.abs(y - CANVAS_HEIGHT);




        if(firstClickX == -1 && firstClickY == -1){
            firstClickX = x;
            firstClickY = y;
        }
        else {
            lines.add(new Line(firstClickX, firstClickY, x, y));
            //System.out.println("Received: x1: "+ firstClickX + " y1:" + firstClickY + " x2:" + x + " y2:" + y);
            firstClickX = -1;
            firstClickY = -1;

        }
    }

    public void actionPerformed(ActionEvent e) {
    }

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
                JPanel p = new JPanel();
                p.setLayout(new FlowLayout());
                frame.setTitle(TITLE);
                JPanel botoes = new JPanel();

                JRadioButton retaButton = new JRadioButton("Naive");
                JRadioButton bressButton = new JRadioButton("Bresenham");

                retaButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(retaButton.isSelected()){
                            algorithimSelected = 0;
                        }
                    }
                });

                bressButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(bressButton.isSelected()){
                            algorithimSelected = 1;
                        }
                    }
                });

                retaButton.setSelected(true);

                JSlider espessura = new JSlider(JSlider.HORIZONTAL, TICK_MIN, TICK_MAX, TICK_INIT);

                espessura.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        thickness = espessura.getValue();
                    }
                });

                JLabel lOpcao = new JLabel("Algoritmo");
                JLabel lEspessura = new JLabel("Espessura");

                ButtonGroup buttonGroup = new ButtonGroup();
                buttonGroup.add(retaButton);
                buttonGroup.add(bressButton);

                JButton botaoCor = new JButton("Cor");
                botaoCor.setOpaque(true);
                botaoCor.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Color color = JColorChooser.showDialog(null, "Escolha a cor para as retas", Color.BLUE);
                        System.out.println(color);

                        if(color != null) {
                            botaoCor.setBackground(color);
                            red = color.getRed()/255;
                            green = color.getGreen()/255;
                            blue = color.getBlue()/255;
                        }
                    }
                });


                botoes.setLayout(new BoxLayout(botoes, BoxLayout.Y_AXIS));
                botoes.add(botaoCor);
                botoes.add(lOpcao);
                botoes.add(retaButton);
                botoes.add(bressButton);
                botoes.add(lEspessura);
                botoes.add(espessura);


                Container pane = frame.getContentPane();
                pane.add(botoes, BorderLayout.LINE_START);
                pane.add(canvas);
                frame.pack();
                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    public Hockey() {
        this.addGLEventListener(this);
        addMouseListener(this);
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
        gl.glColor3f(red, green, blue);
        gl.glPointSize(this.thickness);

        if (algorithimSelected == 0)
            dw = new Bresenham();
        else
            dw = new Naive();

        /*
        *  x1 = 100
        *  x2 = 450
        *
        *  y1 = 50
        *  y2 = 750
        *
        * */
        Line debug = new Line(0,0,0,0);
        for (Line line : lines) {
            dw.drawLine(line.x1, line.y1, line.x2, line.y2, gl);
            debug = line;
        }
        //if (firstClickX != -1)
            //System.out.println("Processed: x1: "+ debug.x1 + " y1:" + debug.y1 + " x2:" +debug.x2 + " y2:" + debug.y2);



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

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e)
    {
        // update the label to show the point
        // through which point mouse is dragged
    }

    public void mouseMoved(MouseEvent e)
    {
    }
}

