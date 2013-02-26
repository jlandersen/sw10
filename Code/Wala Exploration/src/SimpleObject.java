
public class SimpleObject implements ILOL {
	int x = 5;
	
	@Override
	public String toString() {
		return "SimpleObject";
	}

	@Override
	public ILOL hello() {
		return this;
	}	
}
