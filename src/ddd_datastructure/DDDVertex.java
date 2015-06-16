package ddd_datastructure;

import java.util.HashSet;

/**
 * 
 */

/**
 * @author vroger
 *
 */
public abstract class DDDVertex {
	static protected HashSet<Integer> nodeViewed; // help to do the generation
													// of dot properties

	public abstract boolean fRepresentation(float[] chi);

	@Override
	public boolean equals(Object obj) {
		System.out.println("Vertex.equals()");
		return false;
	}

	@Override
	public int hashCode() {
		System.out.println("Vertex.hashCode()");
		return 0;
	}

	/**
	 * @param v
	 *            the vertex we want to know the hashkey
	 * @return the hashKey
	 */
	public static int computeHashKey(DDDVertex v) {
		if (v instanceof Terminal)
			return Terminal.computeHashKey((Terminal) v);

		if (v instanceof NonTerminal)
			return NonTerminal.computeHashKey((NonTerminal) v);

		System.err.println("Type not know");
		System.out.println("Vertex.computeHashKey()");
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vertex []";
	}

	protected String toString(int indent) {
		return "Vertex []";
	}

	/**
	 * 
	 * @return the number of vertices
	 */
	public abstract int numberOfVertices();

	/*------------------------------------------*/
	// Methods to generate the dot file
	/*------------------------------------------*/

	/**
	 * this function allow the user to generate a dot entry to visualize the
	 * graph
	 * 
	 * @return
	 */
	public String generateDot() {
		nodeViewed = new HashSet<Integer>();
		return "digraph {\n" + generateDotDigraphProperties() + "}";
	}

	protected abstract String generateDotDigraphProperties();

	protected abstract String dotId();

	protected abstract String dotProperties();

}
