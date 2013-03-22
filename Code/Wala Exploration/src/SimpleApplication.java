
public class SimpleApplication {
	
	public static void hey() throws Exception {
		new Object();
		
		for(int i = 0; i < 20; i++) { //@ loopbound = 20
			new Object();
			new Object();
		}
		
		throw new Exception();
	}
	
	public static ILOL goodbye(int x) {
		try {
			hey();
		} catch (Exception e) {
			for(int i = 0; i < 500; i++) { //@ loopbound = 500
				new Object();
				new Object();
			}
		}
	
		if(x > 0)
			return new SimpleObject();
		else
			return new TrivialObject();
	}
	
	public static void doWhile() {
		int x = 0;
		do { //@ loopbound = 5
			int y = 0;
			goodbye(5);
			Object n = new Object();
			int z = 2;
			x++;
		} while (x < 5);
	}
	
	public static void whileLoop() {
		int x = 0;
		while(x < 5) { //@ loopbound = 5
			goodbye(5);
			x++;
		}
	}
	
	public static void forLoop() {
		for(int x = 0; x < 5; x++) { //@ loopbound = 5
			goodbye(5);
			int y = 0;
		}
	}
	
	public static void stringTest() {
		String helloString = "Hello String";
		String hello = " ehehhe";
		String trala = hello + helloString;
		String helloConcat = helloString + " Extra ";
	}
	
	public static void arrayTest(String[] args) {
		//int[] array = new int[100013131]; //@ arraycount = 5, arraysize = 50
		//int[] newArr = new int[args.length]; //@ arraycount = 10, arraysize = 150
	}
	
	public static int variableSize = 10;
	public static void main(String[] args) {
		/*
		arrayTest(args);
		int y = 61;
		int[] array = new int[100013131]; //@ arraycount = 5, arraysize = 50
		int yy = 5;
		String[] array2 = new String[1];
		SimpleApplication[] trala = new SimpleApplication[5151];
		int[] newArr = new int[args.length]; //@ arraycount = 10, arraysize = 150
		float[] ff = new float[SimpleApplication.variableSize];
		*/
		Object obj = goodbye(5);
		String str = obj.toString();		
		
		int x = 2;
		if(x == 2) {
			for(int i = 0; i < 20; i++) { //@ loopbound = 20
				obj = new Object();	
			}
		} else {
			obj = new TrivialObject();
			obj = new TrivialObject();
		}
	}
}