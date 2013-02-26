public class SimpleApplication {
	
	public void greetNumberOfTimes(int times) {
		int x = 0;
		while (x < times)  { //@ loopbound = 17
			System.out.println("Hello there!");
			x++;
		}
		
		Object h = new Object();
		
		goodbye();
	}
	
	private void goodbye() {
		Object k = new Object();
		SimpleApplication hej = new SimpleApplication();
		System.out.println("Goodbye..");
	}
	
	private static ILOL returnMe(int x) {
		if (x == 2) {
			return new TrivialObject();
		} else if (x == 5) {
			return new TrivialObject();
		}
		
		return new SimpleObject();
	}
	
	private static void m1() {
		Object n = new Object();
	}
	
	private static void m2() {
		Object n = new Object();
	}
	
	public static void main(String[] args) {
		Object n;
		for(int i = 0; i < 10; i++) { //@ loopbound = 10
			n = new Object();
			int y = 5;
			
			while( y == 2) { //@ loopbound = 2
				
			}
		}
	}

}