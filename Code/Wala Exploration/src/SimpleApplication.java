
public class SimpleApplication {
	
	public static void hey() throws Exception {
		new Object();
		for(int i = 0; i < 20; i++) { //@WCA loop <= 20
			new Object();
			new Object();
		}
		
		throw new Exception();
	}
	
	public static ILOL goodbye(int x) {
		try {
			hey();
		} catch (Exception e) {
			for(int i = 0; i < 500; i++) { //@WCA loop <= 500
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
	
	public static int variableSize = 10;
	public static void arrayTest(String[] args) {
		int[] newArr = new int[variableSize]; //@ length = 100
		
		Object obj = new Object();
	}
	
	public static void main(String[] args) {
		//int length = 1;
		arrayTest(args);
		
		Object obj = null;
		for(int i = 0; i < 20; i++) { //@ loopbound = 20
			obj = new Object();
		}
		//int[] newArray = new int[10];
		//int[] newArr = new int[length]; //@ length = 1241
	}
}