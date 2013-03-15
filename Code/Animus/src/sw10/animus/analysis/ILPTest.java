package sw10.animus.analysis;

import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.Term;

public class ILPTest {
	public void run() {
		SolverFactory factory = new SolverFactoryLpSolve();
		//factory.setParameter(Solver.VERBOSE, 0); 
		//factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		Problem problem = new Problem();
		
		Linear linear = new Linear();
		for(int i = 0; i <= 10; i++) {
			linear.add(1, "bb" + i);
		}
		
		problem.setObjective(linear, OptType.MAX);
		
		linear = new Linear();
		linear.add(1, "f0");
		Constraint constraint = new Constraint(linear, Operator.EQ, 1);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f0");
		linear.add(-1, "f1");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f1");
		linear.add(-1, "f2");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f6");
		linear.add(-1, "f3");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f3");
		linear.add(-1, "f4");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f4");
		linear.add(-1, "f5");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f2");
		linear.add(1, "f5");
		linear.add(-1, "f6");
		linear.add(-1, "f7");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f7");
		linear.add(-1, "f8");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f8");
		linear.add(-1, "f9");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f9");
		linear.add(-1, "ft0");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "ft0");
		constraint = new Constraint(linear, Operator.EQ, 1);
		problem.add(constraint);
		
		/* Loop */
		linear = new Linear();
		linear.add(20, "f2");
		linear.add(-1, "f6");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		/* rem */
		linear = new Linear();
		linear.add(0, "f0");
		linear.add(-1, "bb0");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f0");
		linear.add(-1, "bb1");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f1");
		linear.add(-1, "bb2");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f6");
		linear.add(-1, "bb3");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f3");
		linear.add(-1, "bb4");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f4");
		linear.add(-1, "bb5");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f2");
		linear.add(0, "f5");
		linear.add(-1, "bb6");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(1, "f7");
		linear.add(-1, "bb7");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f8");
		linear.add(-1, "bb8");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "f9");
		linear.add(-1, "bb9");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		linear = new Linear();
		linear.add(0, "ft10");
		linear.add(-1, "bb10");
		constraint = new Constraint(linear, Operator.EQ, 0);
		problem.add(constraint);
		
		for(int i = 0; i <= 10; i++) {
			problem.setVarType("bb" + i, Integer.class);
		}
		
		for(int i = 0; i < 10; i++) {
			problem.setVarType("f" + i, Integer.class);
		}
		problem.setVarType("ft10", Integer.class);
		
		Solver solver = factory.get(); // you should use this solver only once for one problem
		Result result = solver.solve(problem);

		System.out.println("RE " + result.get("f6"));
		
		System.out.println(result);	
	}
	
	public static void main(String[] args) {
		new ILPTest().run();
	}
/*
		max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7 bb8 bb9 bb10;
		
		f0 = 1;
		f0 = f1;
		f1 = f2;
		f6 = f3;
		f3 = f4;
		f4 = f5;
		f2 + f5 = f6 + f7;
		f7 = f8;
		f8 = f9;
		f9 = ft0;
		ft0 = 1;
		
		f6 = 20 f2;
		
		bb0 = 0 f0;
		bb1 = 1 f0;
		bb2 = 0 f1;
		bb3 = 1 f6;
		bb4 = 0 f3;
		bb5 = 0 f4;
		bb6 = 0 f2 + 0 f5;
		bb7 = 1 f7;
		bb8 = 0 f8;
		bb9 = 0 f9;
		bb10 = 0 ft0;
*/
}
