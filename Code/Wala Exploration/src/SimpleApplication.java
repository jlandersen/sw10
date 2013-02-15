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
	
	public static void main(String[] args) {
		SimpleApplication application = new SimpleApplication();
		
		if (args != null) {
			application = new SimpleApplication();
		} else {
			application = new SimpleApplication();
			application = new SimpleApplication();
		}
		
		application.greetNumberOfTimes(5);
	}

}