package program.simple;
import java.lang.Math;

class SimpleJavaProgram {
   public double SquareRoot(int x) {
 	  return Math.sqrt(x);
   }
   public static void main(String args[]) {
   	   SimpleJavaProgram p = new SimpleJavaProgram();
       double result = p.SquareRoot(16);
   }
}