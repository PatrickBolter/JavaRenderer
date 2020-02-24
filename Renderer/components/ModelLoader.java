// Package
package components;

// Project Packages
import components.structs.*;

// Java Packages
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * ModelLoader
 * 
 * Reads a .OBJ file to create a mesh of triangles
*/

public class ModelLoader {
    
    // Array of triangles
    static Triangle[] t = new Triangle[]{new Triangle()};
    // An array of all vectors in the OBJ file
    static Vec3[] v;
    // An arraylist of strings
    static List<String> vec = new ArrayList<String>(), tri = new ArrayList<String>();

    // No constructors, just a static method
    public static Mesh objToMesh(String fname) {
        
        // Open the file into a stream
        try (Stream<String> stream = Files.lines(Paths.get(fname))) {
            // Convert the stream to a list
            List<String> str = stream.collect(Collectors.toList());
            // Filter the stream to give us lines starting with v
            vec = str.stream().filter(line -> line.startsWith("v ")).collect(Collectors.toList());
            // Filter the stream to give us lines starting with f
            tri = str.stream().filter(line -> line.startsWith("f ")).collect(Collectors.toList());
        } catch (IOException e) {
            // Print the error stack trace
            e.printStackTrace();
        }

        // If there are faces listed in the file
        if (tri.size() > 0) {
            // If there are vectors listed in the file
            if (vec.size() > 0) {
                // Create an empty array with the same size as the lines starting with v
                v = new Vec3[vec.size()];
                
                // Create a new list of triangles
                List<Triangle> tlist = new ArrayList<Triangle>();
                // Loop through the length of the vector array
                for(int i = 0; i < v.length; i++) {
                    // Turn the string at i into a Vec3 and store it in the array
                    v[i] = stringToVec(vec.get(i));
                }
                // Foreach string in the triangle string list
                for(String s : tri) {
                    // Turn the string into an array of triangles
                    Triangle[] ts = stringToTri(s);
                    
                    // Add each triangle to the triangle list
                    for(int n = 0; n < ts.length; n++)
                        tlist.add(ts[n]);
                }
                // Turn the triangle list into an array
                t = tlist.toArray(new Triangle[tlist.size()]);
            }
        }
        
        // Return a new mesh with the triangle array
        return new Mesh(t);
        
    }
    
    // Converts a string into a Vec3
    private static Vec3 stringToVec(String s) {
        
        // Split the string using the space char as a delimiter
        String[] arg = s.split(" ");
        
        // Return a new Vec3 using the arguments from the string as the positions
        return new Vec3(Float.parseFloat(arg[1]), Float.parseFloat(arg[2]), Float.parseFloat(arg[3]));
        
    }

    // Converts a string into an array of triangles
    private static Triangle[] stringToTri(String s) {
        
        // // Split the string using the space char as a delimiter
        String[] arg = s.split(" ");
        
        // Create 3 Vec3s using the index listed in the string
        // We need to also split this using "/" as the delimiter
        // as OBJ files also store the vt and vn info for faces
        // We also have to subtract 1 because the vertex indices start at 1
        Vec3 v1 = v[Integer.parseInt(arg[1].split("/")[0]) - 1];
        Vec3 v2 = v[Integer.parseInt(arg[2].split("/")[0]) - 1];
        Vec3 v3 = v[Integer.parseInt(arg[3].split("/")[0]) - 1];
        
        // Declare a new triangle array
        Triangle[] ts = new Triangle[0];
        
        // if the string contains 5 args then it is a quad
        if (arg.length == 5) {
            // Get the 4th vector of the quad
            Vec3 v4 = v[Integer.parseInt(arg[4].split("/")[0]) - 1];
            // Create 2 triangles to represent the quad using the 4 vectors
            ts = new Triangle[2];
            ts[0] = new Triangle(v1, v2, v3);
            ts[1] = new Triangle(v3, v4, v1);
        } else {
            // Otherwise the face is a triangle and we can use 3 vectors
            ts = new Triangle[1];
            ts[0] = new Triangle(v1, v2, v3);
        }
        
        // Return the triangle array
        return ts;
        
    }
}
