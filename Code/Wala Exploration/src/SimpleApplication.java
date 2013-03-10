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
			for(int i = 0; i < 20; i++) { //@ loopbound = 20
				obj = new Object();	
				obj.toString();
			}
		} else {
			obj = new TrivialObject();
			obj = new SimpleObject();
		}
		obj.toString();
	}
}