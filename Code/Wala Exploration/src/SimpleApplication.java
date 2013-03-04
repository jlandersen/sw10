public class SimpleApplication {
	
	private static Object goodbye(int x) {
		if(x > 0)
			return new SimpleObject();
		else
			return new TrivialObject();
	}
	
	public static void main(String[] args) {
		Object obj = goodbye(5);
		if(args[0].equals("hello")) {
			obj = new Object();
		} else {
			obj = new TrivialObject();
			obj = new TrivialObject();
		}
	}
}