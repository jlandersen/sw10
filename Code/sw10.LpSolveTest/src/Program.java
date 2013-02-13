import java.io.File;
import java.io.IOException;

import lpsolve.*;

public class Program {

	public static void main(String[] args) throws LpSolveException, IOException {
		String path = "/Users/Todberg/Desktop/";
		String file = "app.lp";
		
		lpFileCreator creator;
		creator = new lpFileCreator(path, file);
		creator.setObjectiveFunction(lpFileCreator.ObjectiveFunction.MAX);
		creator.addObjective("BB1");
		creator.addObjectives(new String[] { "BB2", "BB3", "BB4", "BB5", "BB6"});
		
		creator.addFlowContraint("BB0", "fs = 1");
		creator.addFlowContraint("BB1", "fs = f1 + f2");
		creator.addFlowContraint("BB2", "f3 = f1");
		creator.addFlowContraint("BB3", "f4 = f2");
		creator.addFlowContraint("BB4", "f5 = f4");
		creator.addFlowContraint("BB5", "f6 = f5");
		creator.addFlowContraint("BB6", "f8 = f6 + f3");
		creator.addFlowContraint("BB7", "f8 = 1");
		
		creator.addAllocationContraint("BB1", "BB1 = 0 f1 + 0 f2");
		creator.addAllocationContraint("BB2", "BB2 = 0 f1");
		creator.addAllocationContraint("BB3", "BB3 = 1 f2");
		creator.addAllocationContraint("BB4", "BB4 = 0 f4");
		creator.addAllocationContraint("BB5", "BB5 = 0 f5");
		creator.addAllocationContraint("BB6", "BB6 = 0 f6 + 0 f3");
		creator.addAllocationContraint("BB7", "BB7 = 0 f8");
		
		creator.writeFile();
		
		LpSolve lp = LpSolve.readLp(creator.getAbsolutePath(), 1, null);
		lp.solve();
		
		// print solution
	    System.out.println("Value of objective function: " + lp.getObjective());
	    double[] var = lp.getPtrVariables();
	    for (int i = 0; i < var.length; i++) {
	      System.out.println("Value of var[" + i + "] = " + var[i]);
	    }

	    // delete the problem and free memory
	    lp.deleteLp();
		
	}
}
