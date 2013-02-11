package sw10;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.ModuleEntry;

public class MyModule implements Module
{
	List<ModuleEntry> entries = new ArrayList<ModuleEntry>();
	
	public void addEntry(ModuleEntry entry)
	{
		this.entries.add(entry);
	}
	
	@Override
	public Iterator<ModuleEntry> getEntries() {
		return entries.iterator();
	}	
}