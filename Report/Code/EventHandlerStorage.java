public abstract class PeriodicEventHandler extends ManagedEventHandler {
	public PeriodicEventHandler(
		PriorityParameters priority,
		AperiodicParameters release, 
		StorageParameters storage) { // <-- difficult part
			... 
	}
	...
}

public abstract class AperiodicEventHandler extends ManagedEventHandler {
	public AperiodicEventHandler(
		PriorityParameters priority,
		AperiodicParameters release, 
		StorageParameters storage) { // <-- difficult part
			...
	}
	...
}