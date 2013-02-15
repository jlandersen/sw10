package analyser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class util {
	private static boolean alreadyShown = false;
	
	public static IClass getIClass(String str, IClassHierarchy cha)
	{
		Iterator<IClass> classes = cha.iterator();
	     
		while (classes.hasNext()) {
			IClass aClass = (IClass) classes.next();
			if (aClass.getName().toString().equals(str))
				return aClass;		
		}
		
		return null;		
	}
	
	public static Collection<IMethod> getMethods(IClassHierarchy cha)
	{
		Iterator<IClass> classes = cha.iterator();
	    List<IMethod> methods = new ArrayList<IMethod>();
	    
		while (classes.hasNext()) {
			IClass aClass = (IClass) classes.next();			
			methods.addAll(aClass.getDeclaredMethods());
		}
		
		return methods;		
	}

	public static void error(String string) {
		System.err.append("Error:" + string + "\n");
		System.exit(1);		
	}

	public static void warn(String string) {
		System.err.append("Warning" + string + "\n");		
	}
	
	public static void warnException() {
		if (alreadyShown == false) {
			System.err.append("Warning: We currently do not check exceptions\n");
			alreadyShown = true; 
		}
	}
}
