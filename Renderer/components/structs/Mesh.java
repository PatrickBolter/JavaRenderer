// Package
package components.structs;

/* Mesh
 * 
 * A class to store an array of triangles
 * 
*/

public class Mesh {

    // Properties
    public Triangle[] tris;

    // Constructors
    public Mesh() { tris = new Triangle[0]; }
    public Mesh(Triangle[] tris) {
        this.tris = tris;
    }

    // Methods
    // For a full featured renderer we would idealy have methods to transform the
    // mesh in local space. This could also be contained in a Transform class.
}