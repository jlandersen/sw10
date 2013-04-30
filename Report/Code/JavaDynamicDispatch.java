public class A  {
	@Override
    public String toString() {
     	return "This is A";
    }
}

public class B  {
	@Override
    public String toString() {
     	return "This is B";
    }
}

public class ToStringPrinter {
    public static void main(String[] args) {
    	Object o = null;
		switch(args[0]) {
			case "A":
				o = new A();
				break;
			case "B":
				o = new B();
				break;
		}
		System.out.println(o.toString());
    }
}