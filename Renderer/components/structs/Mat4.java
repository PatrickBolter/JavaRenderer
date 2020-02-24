// Package
package components.structs;

/* Mat4
 * 
 * Used to represent a float 4x4 matrix and methods to manipulate an instance of Mat4.
 * Contains other useful functions used in 3D graphics and physics.
 * 
*/

public class Mat4 {
    
    // Properties
    public float[][] m = new float[4][4];

    // Constructors
    
    // Default is an identity matrix
    public Mat4() {
        m[0][0] = 1;
        m[1][1] = 1;
        m[2][2] = 1;
        m[3][3] = 1;
    }

    // Transpose a Vec3 onto a Mat4
    public Mat4(Vec3 v) {
        m = new float[4][4];
        m[0][0] = v.x;
        m[1][1] = v.y;
        m[2][2] = v.z;
        m[3][3] = 1;
    }

    // Create an identity matrix scaled to floats x, y, z and w values
    public Mat4(float x, float y, float z, float w) {
        m = new float[4][4];
        m[0][0] = x;
        m[1][1] = y;
        m[2][2] = z;
        m[3][3] = w;
    }

    // Methods

    // Multiply this instance by a Vec3 and return a new Vec3 instance
    public Vec3 vec3Multiply(Vec3 v) {
        Vec3 out = new Vec3();
        out.x = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + v.w * m[3][0];
        out.y = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + v.w * m[3][1];
        out.z = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + v.w * m[3][2];
        out.w = v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + v.w * m[3][3];

        return out;
    }

    // Multiply this instance by another Mat4 instance
    public void matrixMultiply(Mat4 b)
	{
		Mat4 mat = new Mat4();
		for (int c = 0; c < 4; c++)
			for (int r = 0; r < 4; r++)
                mat.m[r][c] = this.m[r][0] * b.m[0][c] + this.m[r][1] * b.m[1][c] + this.m[r][2] * b.m[2][c] + this.m[r][3] * b.m[3][c];
        this.m = mat.m;
	}

    // Override the default toString method to return something useful
    @Override
    public String toString() {
        return "({" + m[0][0] + ", " + m[1][0] + ", " + m[2][0] + ", " + m[3][0] + "}\n"+
               " {" + m[0][1] + ", " + m[1][1] + ", " + m[2][1] + ", " + m[3][1] + "}\n"+
               " {" + m[0][2] + ", " + m[1][2] + ", " + m[2][2] + ", " + m[3][2] + "}\n"+
               " {" + m[0][3] + ", " + m[1][3] + ", " + m[2][3] + ", " + m[3][3] + "})"
        ;
    }

    // Static Methods

    // Same as instance method, multiplies lhs Mat4 and rhs Mat4 to return a new instance
    public static Mat4 matrixMultiply(Mat4 a, Mat4 b)
	{
		Mat4 m = new Mat4();
		for (int c = 0; c < 4; c++)
			for (int r = 0; r < 4; r++)
				m.m[r][c] = a.m[r][0] * b.m[0][c] + a.m[r][1] * b.m[1][c] + a.m[r][2] * b.m[2][c] + a.m[r][3] * b.m[3][c];
		return m;
	}

    // Returns a new Mat4 rotated by float angle on the X axis
    public static Mat4 rotateX(float angle) {

        float theta = angle % 360;
        Mat4 m = new Mat4();
        m.m[1][1] = (float)Math.cos(theta);
        m.m[1][2] = (float)Math.sin(theta);
        m.m[2][1] = (float)-Math.sin(theta);
        m.m[2][2] = (float)Math.cos(theta);
        
        return m;
    }

    // Returns a new Mat4 rotated by float angle on the Y axis
    public static Mat4 rotateY(float angle) {
        
        float theta = angle % 360;
        Mat4 m = new Mat4();
        m.m[0][0] = (float)Math.cos(theta);
        m.m[2][0] = (float)Math.sin(theta);
        m.m[0][2] = (float)-Math.sin(theta);
        m.m[2][2] = (float)Math.cos(theta);
        
        return m;
    }

    // Returns a new Mat4 rotated by float angle on the Z axis
    public static Mat4 rotateZ(float angle) {
        
        float theta = angle % 360;
        Mat4 m = new Mat4();
        m.m[0][0] = (float)Math.cos(theta);
        m.m[0][1] = (float)Math.sin(theta);
        m.m[1][0] = (float)-Math.sin(theta);
        m.m[1][1] = (float)Math.cos(theta);
        
        return m;
    }

    // Returns a new Mat4 translated by given float x, y and z values
    public static Mat4 translate(float x, float y, float z) {

        Mat4 m = new Mat4();
        m.m[3][0] = x;
        m.m[3][1] = y;
        m.m[3][2] = z;
        
        return m;
    }

    // Returns a new Mat4 translated by given Vec3
    public static Mat4 translate(Vec3 v) {
        
        Mat4 m = new Mat4();
        m.m[3][0] = v.x;
        m.m[3][1] = v.y;
        m.m[3][2] = v.z;
        
        return m;
    }

    // Returns a new Mat4 scaled by x, y, and z
    public static Mat4 scale(float x, float y, float z) {

        Mat4 m = new Mat4();
        m.m[0][0] = x;
        m.m[1][1] = y;
        m.m[2][2] = z;
        
        return m;
   }

    // Returns a new Mat4 uniformly scaled by float s
    public static Mat4 scale(float s) {

        Mat4 m = new Mat4();
        m.m[0][0] = s;
        m.m[1][1] = s;
        m.m[2][2] = s;
        
        return m;
    }

    // Returns a new Mat4 scaled by a Vec3 v
    public static Mat4 scale(Vec3 v) {
        
        Mat4 m = new Mat4();
        m.m[0][0] = v.x;
        m.m[1][1] = v.y;
        m.m[2][2] = v.z;
        
        return m;
    }

    // Returns a new Mat4 used to project points into screen space
    // Near and far are float values representing the start z and end z values of the viewer
    // FOV is the field of view
    // Width and height are ideally set to the same as the screen size
    public static Mat4 projection(float near, float far, float fov, float width, float height) {

        // 0.1f, 1000f, 90
        float aspectRatio = height / width;
        float fovRad = (float)(1.0f / Math.tan(Math.toRadians(fov * 0.5f)));
        Mat4 m = new Mat4();
        m.m[0][0] = aspectRatio * fovRad;
        m.m[1][1] = fovRad;
        m.m[2][2] = far / (far - near);
        m.m[3][2] = (-far * near) / (far - near);
        m.m[2][3] = 1;
        m.m[3][3] = 0;

        return m;
    }

    // Creates a new Mat4 that can be multiplied by Vec3 or Mat4 to rotate and translate
    // based on a given position, look direction and up vector
    public static Mat4 pointAt(Vec3 pos, Vec3 target, Vec3 up) {

        // Get the forward direction
        Vec3 forward = Vec3.subtract(target, pos);
        forward.normalize();

        // Calculate the corrected up direction using the new forward vector
        Vec3 a = Vec3.multiply(forward, up.dot(forward));
        Vec3 newUp = Vec3.subtract(up, a);
        newUp.normalize();

        // The right direction is the plane perpendicular to the up and forward directions
        // This is easily found using the cross method
        Vec3 right = Vec3.cross(newUp, forward);

        // Return a new Mat4 using the up, forward and right directions for rotation
        // and the pos x, y, z values for translation
        Mat4 matrix = new Mat4();
		matrix.m[0][0] = right.x;	matrix.m[0][1] = right.y;	matrix.m[0][2] = right.z;	matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = newUp.x;	matrix.m[1][1] = newUp.y;	matrix.m[1][2] = newUp.z;	matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = forward.x;	matrix.m[2][1] = forward.y;	matrix.m[2][2] = forward.z;	matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = pos.x;	    matrix.m[3][1] = pos.y;	    matrix.m[3][2] = pos.z;	    matrix.m[3][3] = 1.0f;
		return matrix;
    }

    // Quick inverse matrix. This will only work for translation and rotation
    // Does not work for scaling. For a full feature 3D renderer this would idealy
    // be written using a proper inverse function
    public static Mat4 quickInverse(Mat4 m) {
        Mat4 matrix = new Mat4();
		matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
		matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
		matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
		matrix.m[3][3] = 1.0f;
		return matrix;
    }
}