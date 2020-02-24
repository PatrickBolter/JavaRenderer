// Package
package components.structs;

/* Vec2
 * 
 * Used to represent a float vector with 2 dimensions. Contains several constructors and methods used to manipulate
 * an instance of the Vec2 class. Methods are identical to Vec3 class however are used for a 2D vector.
 * 
*/

public class Vec2 {
    
    // Properties
    public float x, y, w = 1;
    
    // Constructors
    public Vec2() {}
    public Vec2(float x, float y) { this.x = x; this.y = y; }
    public Vec2(float[] v) { if (v.length >= 1) this.x = v[0]; if (v.length >= 2) this.y = v[1];}

    // Methods
    public void add(Vec2 b) { this.x += b.x; this.y += b.y; }
    public void add(float x, float y, float z) { this.x += x; this.y += y; }

    public void subtract(Vec2 b) { this.x -= b.x; this.y -= b.y; }
    public void subtract(float x, float y, float z) { this.x -= x; this.y -= y; }

    public void multiply(Vec2 b) { this.x *= b.x; this.y *= b.y; }
    public void multiply(float x, float y, float z) { this.x *= x; this.y *= y; }

    public void divide(Vec2 b) { this.x /= b.x; this.y /= b.y; }
    public void divide(float x, float y, float z) { this.x /= x; this.y /= y;  }
    public void divide(float b) { this.x /= b; this.y /= b; }

    public float dot(Vec2 b) {
        return x * b.x + y * b.y;
    }

    public float magnitude() {
        return (float)Math.sqrt(dot(this));
    }

    public void normalize() {
        float mag = magnitude();
        x /= mag;
        y /= mag;
    }

    public Vec2 normalized() {
        float mag = magnitude();
        return new Vec2(x / mag, y / mag);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    // Static Methods
    public static Vec2 add(Vec2 a, Vec2 b) { return new Vec2(a.x + b.x, a.y + b.y); }
    public static Vec2 subtract(Vec2 a, Vec2 b) { return new Vec2(a.x - b.x, a.y - b.y); }
    public static Vec2 multiply(Vec2 a, Vec2 b) { return new Vec2(a.x * b.x, a.y * b.y); }
    public static Vec2 multiply(Vec2 a, float n) { return new Vec2(a.x * n, a.y * n); }
    public static Vec2 divide(Vec2 a, Vec2 b) { return new Vec2(a.x / b.x, a.y / b.y); }

    public static Vec2 intersectPlane(Vec2 p, Vec2 n, Vec2 a, Vec2 b) {
        n = n.normalized();
        float d = -n.dot(p);
        float ad = a.dot(n);
        float bd = b.dot(n);
        float t = (-d - ad) / (bd - ad);
        Vec2 line = Vec2.subtract(b, a);
        Vec2 intersect = Vec2.multiply(line, t);
        return Vec2.add(a, intersect);
    } 

    public static float shortestDisToPlane(Vec2 p, Vec2 n, Vec2 plane) {
        return ((n.x * p.x + n.y * p.y) - n.dot(plane));
    }
}