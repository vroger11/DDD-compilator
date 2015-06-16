package ddd_datastructure;

/**
 * 
 */

/**
 * @author vroger
 *
 */
public class NonTerminal extends DDDVertex {
	private int pos;
	private int neg;
	private ComparativeOperator op;
	private float constant;

	private DDDVertex high;
	private DDDVertex low;

	private int hashCode;

	// flag that help to traverse the graph
	private boolean mark;

	/**
	 * 
	 * @param pos
	 *            the id of the positive variable
	 * @param neg
	 *            the id of the negative variable
	 * @param op
	 *            the operator of the constraint ( < or ≤ )
	 * @param constant
	 *            the constant of the constraint
	 * @param low
	 *            the vertex chosen when the constraint is evaluated to false
	 * @param high
	 *            the vertex chosen when the constraint is evaluated to true
	 * @param hashCode
	 */
	protected NonTerminal(int pos, int neg, ComparativeOperator op,
			float constant, DDDVertex low, DDDVertex high, int hashCode) {
		super();
		this.pos = pos;
		this.neg = neg;
		this.op = op;
		this.constant = constant;
		this.high = high;
		this.low = low;
		this.hashCode = hashCode;
		this.mark = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Vertex#fRepresentation(float[])
	 */
	@Override
	public boolean fRepresentation(float[] chi) {
		return evaluateConstraint(chi) ? high.fRepresentation(chi) : low
				.fRepresentation(chi);
	}

	/**
	 * 
	 * @param v
	 * @return true if the current bound preceded v
	 */
	public boolean precBound(NonTerminal v) {
		return constant < v.constant
				|| (constant == v.constant && op == ComparativeOperator.LT && v.op == ComparativeOperator.LEQ);
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public boolean hasTheSameBound(NonTerminal v) {
		return v.op == op && v.constant == constant;
	}

	/**
	 * 
	 * @param v
	 * @return true if the current vertex and v have the same cstr
	 */
	public boolean hasTheSameCstr(NonTerminal v) {
		return hasTheSameVariables(v) && hasTheSameBound(v);
	}

	/**
	 * 
	 * @param v
	 * @return
	 */
	public boolean hasTheSameVariables(NonTerminal v) {
		return (this.pos == v.pos) && (this.neg == v.neg);
	}

	private boolean evaluateConstraint(float[] chi) {
		switch (op) {
		case LT:
			return chi[pos] - chi[neg] < constant;

		case LEQ:
			return chi[pos] - chi[neg] <= constant;

		default:
			System.err.println("Impossible case");
			System.err.println("NonTerminal.evaluateConstraint()");
			System.exit(1);
			break;
		}

		return false;
	}

	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @return the neg
	 */
	public int getNeg() {
		return neg;
	}

	/**
	 * @return the op
	 */
	public ComparativeOperator getOp() {
		return op;
	}

	/**
	 * @return the high
	 */
	public DDDVertex getHigh() {
		return high;
	}

	/**
	 * @return the low
	 */
	public DDDVertex getLow() {
		return low;
	}

	/**
	 * @return the constant
	 */
	public float getConstant() {
		return constant;
	}

	public static int computeHashKey(int pos, int neg, ComparativeOperator op,
			float constant, DDDVertex low, DDDVertex high) {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(constant);
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + neg;
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + pos;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
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
		if (!(obj instanceof NonTerminal))
			return false;
		NonTerminal other = (NonTerminal) obj;
		if (Float.floatToIntBits(constant) != Float
				.floatToIntBits(other.constant))
			return false;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		if (neg != other.neg)
			return false;
		if (op != other.op)
			return false;
		if (pos != other.pos)
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
		return this.toString(0);
	}

	/**
	 * help to indent the output string
	 */
	protected String toString(int indent) {
		String result = "[(x" + pos + " - x" + neg + " " + op + " " + constant
				+ "),\n";
		for (int i = 0; i < indent; i++) {
			result += "  ";
		}

		result += "high=" + high.toString(indent + 1) + ",\n";
		for (int i = 0; i < indent; i++) {
			result += "  ";
		}

		result += "low=" + low.toString(indent + 1) + "]";
		return result;
	}

	/*------------------------------------------*/
	// Methods to generate the dot file
	/*------------------------------------------*/

	@SuppressWarnings("static-access")
	@Override
	protected String generateDotDigraphProperties() {
		if (super.nodeViewed.contains(hashCode))
			return "";

		super.nodeViewed.add(hashCode);

		return this.dotProperties() + low.generateDotDigraphProperties()
				+ high.generateDotDigraphProperties() + " " + this.dotId()
				+ " -> " + high.dotId() + ";\n " + this.dotId() + " -> "
				+ low.dotId() + " [style=dashed];\n";
	}

	@Override
	protected String dotId() {
		return "nt" + Math.abs(this.hashCode());
	}

	@Override
	protected String dotProperties() {
		return " " + this.dotId() + " [label=\"x" + pos + " - x" + neg + " "
				+ (op == ComparativeOperator.LEQ ? "<= " : "< ") + constant
				+ "\"];\n";
	}

	@Override
	public int numberOfVertices() {
		int result = 1;
		mark = !mark;

		if (low instanceof NonTerminal && mark != ((NonTerminal) low).mark)
			result += low.numberOfVertices();

		if (high instanceof NonTerminal && mark != ((NonTerminal) high).mark)
			result += high.numberOfVertices();

		return result;
	}

}
