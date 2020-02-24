// Package
package components;

// Project packages
import components.structs.*;

// Java packages
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

/* Frame
 * 
 * An extension of the JPanel class. Represents a panel that draws a 3D scene.
 * 
*/

@SuppressWarnings("serial") 
public class Frame extends JPanel {

    // Properties
    public int[] input = new int[KeyEvent.CHAR_UNDEFINED];  // Captured inputs
    static int fps, frames;                                 // Used to calculate and display frames per second
    int[][] frameBuffer;                                    // The color information of the frame
    float[][] zBuffer;                                      // The depth information of the frame (not implemented)
    Mesh[] meshes = new Mesh[256];                          // An array of meshes to draw (increase size to allow more meshes to be drawn)
    int meshCount = 0;                                      // Total number of meshes in the array
    int width = 1280;                                       // Frame's width
    int height = 720;                                       // Frame's height
    Mat4 projMat;                                           // The projection matrix for this frame
    Mat4 rx = new Mat4(), rz = new Mat4();                  // Stores an X and Z rotation matrix
    float angle = 0;                                        // Used to rotate rotation matrices
    public Vec3 camera = new Vec3();                        // A Vec3 representing the camera's position in world space
    Vec3 lookDir = new Vec3();                              // The direction the camera is looking
    Mat4 view;                                              // Stores a view matrix
    float yaw;                                              // The yaw rotation of the camera
    float near = 0.05f;                                     // The z value for the near plane
    List<Triangle> drawTris;                                // List of triangles to raster

    // A task that updates the frame count every second
    TimerTask updateFPS = new TimerTask() {
        public void run() {
            // Frames is the total frames drawn this second
            fps = frames;
            // Set frames back to 0
            frames = 0;
        }
    };
    // Timer used to update the frames per second
    Timer t = new Timer();

    // Constructors
    public Frame(int width, int height) {
        // Schedule the update task for every second
        t.scheduleAtFixedRate(updateFPS, 1000, 1000);

        // Create a blank frame and z buffer
        this.frameBuffer = new int[width][height];
        this.zBuffer = new float[width][height];

        // The width and height of the frame
        this.width = width;
        this.height = height;

        // Create the projection matrix using the given width and height
        projMat = Mat4.projection(near, 1000f, 90, width, height);
    }

    // Methods

    // Override the paint function of the JPanel class
    @Override
    public void paint(Graphics g) {

        // Call the original paintComponent method
        super.paintComponent(g);

        // Rotate
        angle += 0.01f;

        // Clears the buffer to 0 values
        clearBuffer();

        // Only draw is there is something to draw
        if (meshCount > 0) {
            
            // Rotate matrices
            rx = Mat4.rotateX(angle);
            rz = Mat4.rotateZ(angle);

            // Compose transform matrix
            Mat4 transform = Mat4.matrixMultiply(new Mat4(), rz);
            transform.matrixMultiply(rx);
            transform.matrixMultiply(Mat4.translate(0, 0, 15 - 10 * (angle / 360f)));

            // Create camera
            Vec3 forward = Vec3.multiply(lookDir, 0.1f);

            // Use the WASD keys to rotate the camera and move forward and backwards
            if (input[KeyEvent.VK_W] == 1) {
                camera.add(forward);
            }
            if (input[KeyEvent.VK_S] == 1) {
                camera.add(Vec3.multiply(forward,new Vec3(-1, -1, -1)));
            }
            if (input[KeyEvent.VK_A] == 1) {
                yaw -= 0.01f;
            }

            if (input[KeyEvent.VK_D] == 1) {
                yaw += 0.01f;
            }

            // Set the up and look directions
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 target = new Vec3( 0, 0, 1);

            // Create a rotation matrix based on the yaw
            Mat4 rotated = Mat4.rotateY(yaw);
            lookDir = rotated.vec3Multiply(target);
            target = Vec3.add(camera, lookDir);
            
            // Create the view matrix
            Mat4 cameraMat = Mat4.pointAt(camera, target, up);
            view = Mat4.quickInverse(cameraMat);

            // Create an empty list of triangles
            drawTris = new ArrayList<Triangle>();

            // Drawing process:
            // project to world -> project to view -> clip against near plane
            // -> project to screen -> clip against screen edges -> raster tris

            // Loop through each mesh in the scene
            for (int i = 0; i < meshCount; i++) {
                // Project the mesh's triangles ready to be drawn to the screen
                drawMesh(meshes[i], transform);
            }

            // Draw the projected triangles
            drawTriangles(drawTris, true, false);
        }

        // Create an image using the frame buffer
        Image img = createBufferedImage();
        // Draw the image
        g.drawImage(img, 0, 0, this);
        
        // Draw debug info
        g.setColor(Color.WHITE);
        g.drawString("FPS: "+fps,20,36);
        g.drawString("Camera: "+camera.toString(),20,50);

        // Increment the total frames drawn in this second
        frames++;
    }

    // Add a mesh to the array and increment mesh count
    public int addMesh(Mesh m) {
        meshes[meshCount] = m;
        return meshCount++;
    }

    // Remove the mesh at given index, take last mesh and move it to this index
    public void removeMesh(int i) {
        // Decrement the mesh count
        meshCount--;
        // if the index is lower than the total meshes
        if (i < meshCount) {
            // insert the last mesh into this index
            meshes[i] = meshes[meshCount];
            // set the last mesh to an empty mesh
            meshes[meshCount] = new Mesh();
        } else
            meshes[i] = new Mesh(); // Else set this index to an empty mesh
    }

    // Approximate the Z index of triangle a given their Z values
    // Naive approach, use depth buffering for more accurate result
    private boolean sortTri(Triangle a, Triangle b) {
        float z1 = (a.p[0].z + a.p[1].z + a.p[2].z) / 3;
        float z2 = (b.p[0].z + b.p[1].z + b.p[2].z) / 3;
        return z1 < z2;
    }

    // Projects a meshes triangles to world space, view space and screen space
    private void drawMesh(Mesh m, Mat4 transform) {
        // Loop through all of this meshes triangles
        for(int t = 0; t < m.tris.length; t++) {
            
            // Copy to a new triangle, we don't want to transform the original triangle
            Triangle p = new Triangle(m.tris[t].p[0], m.tris[t].p[1], m.tris[t].p[2]);
            p.color = m.tris[t].color;

            // Apply local transformations to the mesh
            p.p[0] = transform.vec3Multiply(p.p[0]);
            p.p[1] = transform.vec3Multiply(p.p[1]);
            p.p[2] = transform.vec3Multiply(p.p[2]);

            // Backface culling
            Vec3 normal, line1, line2;
            // Calculate the normal direction of this triangle
            line1 = Vec3.subtract(p.p[1], p.p[0]);
            line2 = Vec3.subtract(p.p[2], p.p[0]);
            normal = Vec3.cross(line1, line2);
            normal.normalize();
            
            // Only continue if the triangle is facing towards the screen
            if (normal.x * (p.p[0].x - camera.x) +
                normal.y * (p.p[0].y - camera.y) +
                normal.z * (p.p[0].z - camera.z) < 0) {

                // Illumination
                Vec3 light = new Vec3(0, 0, -1f);
                light.normalize();
                float dp = clamp(normal.dot(light), 0, 1);
                p.color = p.scaleColor(dp);
                
                // Convert to view space
                p.p[0] = view.vec3Multiply(p.p[0]);
                p.p[1] = view.vec3Multiply(p.p[1]);
                p.p[2] = view.vec3Multiply(p.p[2]);

                // Clip the triangle against the near plane
                Triangle[] clip = p.clipAgainstPlane(new Vec3(0, 0, near), new Vec3(0, 0, 1));

                // Loop through the resulting clipped triangles
                for (int n = 0; n < clip.length; n++) {
                    // Project to 2D screen space
                    clip[n].p[0] = projMat.vec3Multiply(clip[n].p[0]);
                    clip[n].p[1] = projMat.vec3Multiply(clip[n].p[1]);
                    clip[n].p[2] = projMat.vec3Multiply(clip[n].p[2]);

                    // Scale into screen space
                    clip[n].p[0].divide(clip[n].p[0].w);
                    clip[n].p[1].divide(clip[n].p[1].w);
                    clip[n].p[2].divide(clip[n].p[2].w);

                    Vec3 offset = new Vec3(1,1,0);
                    clip[n].p[0].add(offset); clip[n].p[1].add(offset); clip[n].p[2].add(offset);
                    clip[n].p[0].x *= 0.5f * width;clip[n].p[0].y *= 0.5f * height;
                    clip[n].p[1].x *= 0.5f * width;clip[n].p[1].y *= 0.5f * height;
                    clip[n].p[2].x *= 0.5f * width;clip[n].p[2].y *= 0.5f * height;
                    
                    // Add the triangles to the draw list
                    if (drawTris.size() == 0)
                        drawTris.add(clip[n]);
                    else {
                        boolean insert = false;
                        for(int i = 0; i < drawTris.size(); i++) {
                            // If this triangle is closer to the near plane put it infront
                            if (!sortTri(clip[n], drawTris.get(i))) {
                                insert = true;
                                drawTris.add(i, clip[n]);
                                break;
                            }
                        }
                        // If we hadn't inserted the triangle, add it to the end
                        if (!insert) drawTris.add(clip[n]);
                    }
                }
            }
        }
    }
    
    // Clears the frame buffer to black
    private void clearBuffer() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                frameBuffer[x][y] = Color.BLACK.getRGB();
                zBuffer[x][y] = 1000;
            }
        }
    }

    // Creates an Image instance representing the frame buffer
    private Image createBufferedImage() {

        // Create a buffered image instance
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Loop through the frame buffer to change the image's pixels
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                img.setRGB(x, y, frameBuffer[x][y]);
            }
        }
        
        // Return the buffered image
        return img;
    }

    // Draws a line using Bresenham's principles of integer incremental error
    private void drawLine(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int sx = x1 < x2 ? 1 : -1;
        int dy = -Math.abs(y2 - y1);
        int sy = y1 < y2 ? 1 : -1;
        int err = dx + dy;
        while(true) {
            frameBuffer[x1][y1] = color;
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2*err;
            if (e2 >= dy) {
                err += dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    // Rasterize triangle into frame buffer
    private void drawTriangle(Triangle t) {
        
        // Get the traingle's bounding box
        int[] bbox = t.getBBox(width-1, height-1);
        
        // loop through the min and max values of the bounding box
        for (int y = bbox[1]; y < bbox[3]; y++) {
            for (int x = bbox[0]; x < bbox[2]; x++) {
                // If the point is in the triangle
                if (t.pointInTriangle(x, y)) { 
                    // Draw it's color to the frame buffer
                    frameBuffer[x][y] = t.color;
                }
            }
        }
    }

    // Draws a wire frame of the triangle directly to the panel
    private void drawWireFrame(Triangle t, int c) {

        // Clamp the values to the frame's bounds
        int x1, y1, x2, y2, x3, y3;
        x1 = (int)clamp(t.p[0].x, 0, width-1); y1 = (int)clamp(t.p[0].y, 0, height-1);
        x2 = (int)clamp(t.p[1].x, 0, width-1); y2 = (int)clamp(t.p[1].y, 0, height-1);
        x3 = (int)clamp(t.p[2].x, 0, width-1); y3 = (int)clamp(t.p[2].y, 0, height-1);

        // Draw lines between the triangle's 3 points
        drawLine(x1, y1, x2, y2, c);
        drawLine(x1, y1, x3, y3, c);
        drawLine(x2, y2, x3, y3, c);
    }

    // Draws all triangles in a given list of triangles
    // fill: If true draws a flat shaded triangle
    // wireFrame: If true draws a wire frame of the triangle
    private void drawTriangles(List<Triangle> t, boolean fill, boolean wireFrame) {
        
        // We don't need to do anything if no triangles are visible
        if (t.size() == 0)
            return;
        // Used in clipping algorithm
        int addTris = 1;
        // Need to loop through all 4 edges of the screen
        for (int i = 0; i < 4; i++) {

            // Set to 1 initially so we always have at least 1 triangle to test
            while(addTris > 0) {

                // Get the first triangle from the list
                Triangle test = t.remove(0);
                // Create an empty array of Triangles
                Triangle[] add = new Triangle[0];
                // Decrement the addTris, once this is 0 we break out of the loop
                addTris--;

                // switch statement, selects which edge of the screen to clip against
                switch (i) {
                    // Clip against top of the screen
                    case 0: add = test.clipAgainstPlane(new Vec3(0, 0, 0), new Vec3(0, 1, 0)); break;
                    // Clip against bottom of the screen
                    case 1: add = test.clipAgainstPlane(new Vec3(0, height - 1, 0), new Vec3(0, -1, 0)); break;
                    // Clip against left of the screen
                    case 2: add = test.clipAgainstPlane(new Vec3(0, 0, 0), new Vec3(1, 0, 0)); break;
                    // Clip against right of the screen
                    case 3: add = test.clipAgainstPlane(new Vec3(width - 1, 0, 0), new Vec3(-1, 0, 0)); break;
                }

                // Loop through the add array and add clipped triangles to the triangle list
                for(int n = 0; n < add.length; n++) { t.add(add[n]); }

            }

            // Set addTris to the current size of the triangle list
            addTris = t.size();
        }

        // Loop through the triangle list
        for (int i = 0; i < t.size(); i++) {

            // Draw flat shaded triangle at index i
            if (fill)
                drawTriangle(t.get(i));

            // Draw wire frame of triangle at index i
            if (wireFrame)
                drawWireFrame(t.get(i), Color.white.getRGB());
        }
    }

    // Clamps a given float between min and max values
    public float clamp(float a, float min, float max) {
        return Math.min(Math.max(a,min),max);
    }

}

