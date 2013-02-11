
public class SimpleApplication {

	public void greetNumberOfTimes(int times) {
		int x = 0;
		while (x < times)  {
			System.out.println("Hello there!");
			x++;
		}
		
		goodbye();
	}
	
	private void goodbye() {
		System.out.println("Goodbye..");
	}
	
	public static void main(String[] args) {
		SimpleApplication application = new SimpleApplication();
		
		application.greetNumberOfTimes(5);
	}

}
