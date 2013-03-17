
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
	
	public static void main(String[] args) {
		Object obj = goodbye(5);
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