package parse_smt_problem.heuristics.relation_graph;

import java.util.HashMap;

public class Vertex {
	private int varNumber;
	private HashMap<Vertex, Float> linked; // first the vertex linked, second the CoM
	private float CoM;
	
	/**
	 * @param var1
	 * @param var2
	 */
	public Vertex(int var1) {
		super();
		this.varNumber = var1;
		linked = new HashMap<Vertex, Float>();
		CoM = 0; 
	}

	public void addRelation(Vertex v) {
		if (!linked.containsKey(v))
			linked.put(v, 0.f);
	}

	public int numberRelations() {
		return linked.size();
	}

	/**
	 * @return the varNumber
	 */
	public int getVarNumber() {
		return varNumber;
	}
	
	
	/**
	 * @return the coM
	 */
	public float getCoM() {
		return CoM;
	}

	/**
	 * @param coM the coM to set
	 */
	public void setCoM(float coM) {
		CoM = coM;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + varNumber;
		return result;
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
		if (!(obj instanceof Vertex))
			return false;
		Vertex other = (Vertex) obj;
		if (varNumber != other.varNumber)
			return false;
		return true;
	}

	/**
	 * @return the linked
	 */
	public HashMap<Vertex, Float> getLinked() {
		return linked;
	}

}
