// Project Packages
import components.structs.*;
import components.Frame;
import components.ModelLoader;

// Java Packages
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame; 
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

/* Entry
 * 
 * The main class for this project represents the entry point into the program.
 * Creates a frame instance and captures keyboard input.
 * 
*/

public class Entry {

    static JFrame frame;
    static public Frame f;
    static GraphicsConfiguration gc;

    public static void main(String[] args) {
        int width = 640;
        int height = 360;
        
        // Create the main window
        frame = new JFrame("Renderer", gc);
        f = new Frame(width, height);
        frame.getContentPane().add(f);
        frame.addKeyListener(new keyListener());
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Load a model into a mesh object, add to mesh list
        // We are using the Blender mascot Suzanne as our test model
        Mesh test = ModelLoader.objToMesh(System.getProperty("user.dir")+"\\funnymunkey.obj");
        f.addMesh(test);

        // Main loop
        while (true) {

            // Redraw the screen
            frame.repaint();

        }
    }


    // Used to capture input
    private static class keyListener extends KeyAdapter{
        @Override

        // Capture key presses
        public void keyPressed(KeyEvent e) {
                
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                Entry.frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                break;

                // Set the input key to 1 when pressed
                default:
                    f.input[e.getKeyCode()] = 1;
                break;
            }
                
        }

        // Capture key releases
        @Override
        public void keyReleased(KeyEvent e) {
            // Set the input key to 0 when released
            f.input[e.getKeyCode()] = 0;
        }
    }
}