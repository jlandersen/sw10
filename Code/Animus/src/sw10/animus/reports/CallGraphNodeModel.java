package sw10.animus.reports;

/*
 * Model for GSON serialization
 */
public class CallGraphNodeModel {
	public String name;
	public String guid;
	public String color;
	public CallGraphNodeModel[] children;
}
