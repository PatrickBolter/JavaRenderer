// Package
package components.structs;

// Java Packages
import java.awt.Color;

/* Triangle
 * 
 * Used to represent a triangle using 3 points represented by Vec3.
 * Contains other useful functions used in 3D graphics and physics.
 * Mostly used for screen space calculations, the Z values are useful
 * for implementing depth buffering and other features.
 * 
*/

public class Triangle {

    // Properties
    public Vec3[] p;                        // Array of points
    public int color = Color.RED.getRGB();  // Default color is red

    // Constructors
    public Triangle() { p = new Vec3[]{new Vec3(), new Vec3(), new Vec3()}; }
    public Triangle(Vec3 v1, Vec3 v2, Vec3 v3) { p = new Vec3[]{new Vec3(v1.x, v1.y, v1.z), new Vec3(v2.x, v2.y, v2.z), new Vec3(v3.x, v3.y, v3.z)}; }
    public Triangle(Vec3[] v) { p = v; }

    // Methods

    // Returns the area of the triangle
    public float area(float x1, float x2, float x3, float y1, float y2, float y3) {
        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0f);
    }

    // Returns if a given x y point is within the triangle's area
    public boolean pointInTriangle(int x, int y) {

        // Get the area of the this triangle
        float a = area(Math.round(p[0].x), Math.round(p[1].x), Math.round(p[2].x), Math.round(p[0].y), Math.round(p[1].y), Math.round(p[2].y));
        // Get the area of 3 new triangles using this triangle's points and given x, y values
        float a1 = area(x, Math.round(p[1].x), Math.round(p[2].x), y, Math.round(p[1].y), Math.round(p[2].y));
        float a2 = area(Math.round(p[0].x), x, Math.round(p[2].x), Math.round(p[0].y), y, Math.round(p[2].y));
        float a3 = area(Math.round(p[0].x), Math.round(p[1].x), x, Math.round(p[0].y), Math.round(p[1].y), y);

        // If the area of all 3 new triangles is equal to the original area then the point is in this triangle
        return (a == a1 + a2 + a3);
    }

    // Scales the color of this triangle by a given float
    // Used to dim the triangle
    public int scaleColor(float scale) {
        Color c = new Color(color);
        int r, g, b;
        r = Math.max(0,Math.min(255,(int)(c.getRed() * scale)));
        g = Math.max(0,Math.min(255,(int)(c.getGreen() * scale)));
        b = Math.max(0,Math.min(255,(int)(c.getBlue() * scale)));
        return new Color(r,g,b,1).getRGB();
    }

    // Gets the bounding box of the triangle in 2D screen space
    public int[] getBBox(int w, int h) {
        int[] b = new int[4];
        b[0] = ((int)Math.max(0, Math.min(Math.min(Math.floor(p[0].x), Math.floor(p[1].x)), Math.floor(p[2].x))));
        b[1] = (int)Math.max(0, Math.min(Math.min(Math.floor(p[0].y), Math.floor(p[1].y)), Math.floor(p[2].y)));
        b[2] = (int)Math.min(w, Math.max(Math.max(Math.ceil(p[0].x), Math.ceil(p[1].x)), Math.ceil(p[2].x)));
        b[3] = (int)Math.min(h, Math.max(Math.max(Math.ceil(p[0].y), Math.ceil(p[1].y)), Math.ceil(p[2].y)));
        return b;
    }

    // Clips the triangle against a given plane in normal direction n
    // Returns either 0, 1 or 2 triangles in a Triangle array
    public Triangle[] clipAgainstPlane(Vec3 plane, Vec3 n) {

        // Make sure the direction is normalized (should always be anyway)
        n.normalize();

        // 2 arrays to hold the points inside and outside of the given plane
        Vec3[] inside = new Vec3[3]; int i = 0;
        Vec3[] outside = new Vec3[3]; int o = 0;

        // Get the distance of the point from the plane in n direction
        float d0 = Vec3.shortestDisToPlane(p[0], n, plane);
        float d1 = Vec3.shortestDisToPlane(p[1], n, plane);
        float d2 = Vec3.shortestDisToPlane(p[2], n, plane);

        // If the distance is higher than 0 we are on the positive side of the plane
        if (d0 >= 0) { inside[i++] = this.p[0]; } else { outside[o++] = this.p[0]; }
        if (d1 >= 0) { inside[i++] = this.p[1]; } else { outside[o++] = this.p[1]; }
        if (d2 >= 0) { inside[i++] = this.p[2]; } else { outside[o++] = this.p[2]; }

        // If none of the points are inside, return and empty triangle array
        if (i == 0) return new Triangle[0];
        
        // If all points are inside, return the original triangle
        if (i == 3) {
            return new Triangle[]{this};
        }

        // If one point is inside we need to create 1 triangle
        if (i == 1 && o == 2) {
            Triangle t1 = new Triangle();
            t1.color = this.color;

            // Use the point that is inside for the first point
            t1.p[0] = inside[0];
            // Use the 2 points that intersect the plane using the outside points
            t1.p[1] = Vec3.intersectPlane(plane, n, inside[0], outside[0]);
            t1.p[2] = Vec3.intersectPlane(plane, n, inside[0], outside[1]);
            return new Triangle[]{t1};
        }

        // If 2 points are inside we need to create 2 triangles
        if (i == 2 && o == 1) {
            Triangle t1 = new Triangle();
            Triangle t2 = new Triangle();
            t1.color = this.color; t2.color = this.color;

            // First triangle uses the 2 inside points and the intersect of the plane and outside point
            t1.p[0] = inside[0];
            t1.p[1] = inside[1];
            t1.p[2] = Vec3.intersectPlane(plane, n, inside[0], outside[0]);

            // Second triangle can use one of the points of the first triangle
            t2.p[0] = inside[1];
            t2.p[1] = t1.p[2];
            t2.p[2] = Vec3.intersectPlane(plane, n, inside[1], outside[0]);
            return new Triangle[]{t1, t2};
        }
        return new Triangle[]{this};
    }
}