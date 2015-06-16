package ddd_datastructure;

/**
 * 
 */

/**
 * @author vroger
 *
 */
public class Terminal extends DDDVertex {
	private boolean value;

	protected Terminal(boolean value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Vertex#fRepresentation(float[])
	 */
	@Override
	public boolean fRepresentation(float[] chi) {
		return value;
	}

	/**
	 * Compute the HashKey of the vertex v
	 * 
	 * @param v
	 * @return
	 */
	public static int computeHashKey(boolean value) {
		return value ? 1 : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return computeHashKey(this.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Terminal))
			return false;
		Terminal other = (Terminal) obj;
		if (value != other.value)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + value + "]";
	}

	protected String toString(int indent) {
		return "[" + value + "]";
	}

	/*------------------------------------------*/
	// Methods to generate the dot file
	/*------------------------------------------*/

	@SuppressWarnings("static-access")
	@Override
	protected String generateDotDigraphProperties() {
		int hashCode = this.hashCode();

		if (super.nodeViewed.contains(hashCode))
			return "";

		super.nodeViewed.add(hashCode);

		return this.dotProperties();
	}

	@Override
	protected String dotId() {
		return "t" + this.hashCode();
	}

	@Override
	protected String dotProperties() {
		return " " + this.dotId() + " [label=\"" + (value ? "1" : "0")
				+ "\" shape=box];\n";
	}

	@Override
	public int numberOfVertices() {
		return 0;
	}

}
