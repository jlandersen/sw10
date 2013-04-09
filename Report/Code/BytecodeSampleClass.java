..
public static void main(java.lang.String[]);
 Code:
 Stack=2, Locals=3, Args_size=1
 0:   getstatic #2; //Field java/lang/System.out:Ljava/io/PrintStream;
 3:   ldc #3; //String This is a simple addition
 5:   invokevirtual #4; //Method java/io/PrintStream.println:(Ljava/lang/String;)V
 8:   iconst_5
 9:   istore_1
 10:  iload_1
 11:  iconst_2
 12:  iadd
 13:  istore_2
 14:  return
 ..
}
..