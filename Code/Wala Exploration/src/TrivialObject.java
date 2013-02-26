
public class TrivialObject implements ILOL {
	int x = 5;
	
	@Override
	public String toString() {
		return "TrivialObject";
	}

	@Override
	public ILOL hello() {
		return this;
	}	
}
