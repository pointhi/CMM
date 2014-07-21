package at.jku.ssw.cmm.debugger;

import at.jku.ssw.cmm.compiler.Node;

public interface Debugger {

	public boolean step(Node arg0);

	public void abort(String message, Node node);
}