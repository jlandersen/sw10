package sw10.spideybc.reports;

import com.ibm.wala.ipa.callgraph.CGNode;

/*
 * Model for GSON serialization
 */
public class CallGraphNodeModel {
	public transient CGNode node;
	public String name;
	public String signature;
	public long cost;
	public String guid;
	public String color;
	public CallGraphNodeModel[] children;
}
