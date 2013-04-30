@SCJAllowed
public interface Safelet<MissionLevel extends Mission> {
	public long immortalMemorySize();
	...
}

@SCJAllowed
public abstract class Mission {
	abstract public long missionMemorySize();
	...
}