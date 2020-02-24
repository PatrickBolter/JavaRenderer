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

public class ModelLoader {
    static Triangle[] t = new Triangle[]{new Triangle()};
    static Vec3[] v;
    static List<String> vec = new ArrayList<String>(), tri = new ArrayList<String>();

    public static Mesh objToMesh(String fname) {
        

        try (Stream<String> stream = Files.lines(Paths.get(fname))) {
            List<String> str = stream.collect(Collectors.toList());
            vec = str.stream().filter(line -> line.startsWith("v ")).collect(Collectors.toList());
            tri = str.stream().filter(line -> line.startsWith("f ")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tri.size() > 0) {
            if (vec.size() > 0) {
                v = new Vec3[vec.size()];
                List<Triangle> tlist = new ArrayList<Triangle>();
                for(int i = 0; i < v.length; i++) {
                    v[i] = stringToVec(vec.get(i));
                }
                for(String s : tri) {
                    Triangle[] ts = stringToTri(s);
                    for(int n = 0; n < ts.length; n++)
                    tlist.add(ts[n]);
                }
                t = tlist.toArray(new Triangle[tlist.size()]);
            }
        }
        
        return new Mesh(t);
    }
    
    private static Vec3 stringToVec(String s) {
        String[] arg = s.split(" ");
        return new Vec3(Float.parseFloat(arg[1]), Float.parseFloat(arg[2]), Float.parseFloat(arg[3]));
    }

    private static Triangle[] stringToTri(String s) {
        String[] arg = s.split(" ");
        Vec3 v1 = v[Integer.parseInt(arg[1].split("/")[0]) - 1];
        Vec3 v2 = v[Integer.parseInt(arg[2].split("/")[0]) - 1];
        Vec3 v3 = v[Integer.parseInt(arg[3].split("/")[0]) - 1];
        Triangle[] ts = new Triangle[0];
        if (arg.length == 5) {
            Vec3 v4 = v[Integer.parseInt(arg[4].split("/")[0]) - 1];
            ts = new Triangle[2];
            ts[0] = new Triangle(v1, v2, v3);
            ts[1] = new Triangle(v3, v4, v1);
        } else {
            ts = new Triangle[1];
            ts[0] = new Triangle(v1, v2, v3);
        }
        
        return ts;
    }
}