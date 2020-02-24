// Package
package components.structs;

/* Vec3
 * 
 * Used to represent a float vector with 3 dimensions. Contains several constructors and methods used to manipulate
 * an instance of the Vec3 class.
 * 
*/

public class Vec3 {
    
    // Properties
    public float x, y, z, w = 1;
    
    // Constructors
    public Vec3() {}
    public Vec3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    public Vec3(float[] v) { if (v.length >= 1) this.x = v[0]; if (v.length >= 2) this.y = v[1]; if (v.length >= 3) this.z = v[2]; }

    // Methods

    // Add the x, y and z values from another Vec3 to this instance
    public void add(Vec3 b) { this.x += b.x; this.y += b.y; this.z += b.z; }
    // Add an x, y and z float value to this instance's properties
    public void add(float x, float y, float z) { this.x += x; this.y += y; this.z += z; }

    // Subtract
    public void subtract(Vec3 b) { this.x -= b.x; this.y -= b.y; this.z -= b.z; }
    public void subtract(float x, float y, float z) { this.x -= x; this.y -= y; this.z -= z; }

    // Multiply
    public void multiply(Vec3 b) { this.x *= b.x; this.y *= b.y; this.z *= b.z; }
    public void multiply(float x, float y, float z) { this.x *= x; this.y *= y; this.z *= z; }
    // Multiply x, y and z by a float value
    public void multiply(float b) { this.x *= b; this.y *= b; this.z *= b; }

    // Divide
    public void divide(Vec3 b) { this.x /= b.x; this.y /= b.y; this.z /= b.z; }
    public void divide(float x, float y, float z) { this.x /= x; this.y /= y; this.z /= z; }
    public void divide(float b) { this.x /= b; this.y /= b; this.z /= b; }

    // Returns the dot product of this instance and another Vec3.
    public float dot(Vec3 b) {
        return x * b.x + y * b.y + z * b.z;
    }

    // Returns the length of this Vec3 using Pythogoras theorum
    public float magnitude() {
        return (float)Math.sqrt(dot(this));
    }

    // Normalizes this instance's properties to a scale from 0 - 1.
    public void normalize() {
        float mag = magnitude();
        x /= mag;
        y /= mag;
        z /= mag;
    }

    // Returns a new Vec3 with normalized properties
    public Vec3 normalized() {
        float mag = magnitude();
        return new Vec3(x / mag, y / mag, z / mag);
    }

    // Overrides the toString method to return a useful string
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    // Static Methods

    // Same as instance methods but return a new Vec3 between a lhs and rhs Vec3
    public static Vec3 add(Vec3 a, Vec3 b) { return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z); }
    public static Vec3 subtract(Vec3 a, Vec3 b) { return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z); }
    public static Vec3 multiply(Vec3 a, Vec3 b) { return new Vec3(a.x * b.x, a.y * b.y, a.z * b.z); }
    public static Vec3 multiply(Vec3 a, float n) { return new Vec3(a.x * n, a.y * n, a.z * n); }
    public static Vec3 divide(Vec3 a, Vec3 b) { return new Vec3(a.x / b.x, a.y / b.y, a.z / b.z); }
    public static Vec3 divide(Vec3 a, float n) { return new Vec3(a.x / n, a.y / n, a.z / n); }

    // Returns the cross product of 3 Vec3 instances
    public static Vec3 cross(Vec3 a, Vec3 b) {
        Vec3 c = new Vec3();
        c.x = a.y * b.z - a.z * b.y;
        c.y = a.z * b.x - a.x * b.z;
        c.z = a.x * b.y - a.y * b.x;
        return c;
    }

    // Returns a Vec3 representing a point between 2 Vec3 (a and b)
    // along a plane (p) in normal direction (n)
    public static Vec3 intersectPlane(Vec3 p, Vec3 n, Vec3 a, Vec3 b) {
        n = n.normalized();
        float d = -n.dot(p);
        float ad = a.dot(n);
        float bd = b.dot(n);
        float t = (-d - ad) / (bd - ad);
        Vec3 line = Vec3.subtract(b, a);
        Vec3 intersect = Vec3.multiply(line, t);
        return Vec3.add(a, intersect);
    } 

    // Returns the distance that is shortest from point p along plane in n direction
    public static float shortestDisToPlane(Vec3 p, Vec3 n, Vec3 plane) {
        return (n.dot(p) - n.dot(plane));
    }
}