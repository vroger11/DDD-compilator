package parse_smt_problem.heuristics.relation_graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
	private HashMap<Integer, Vertex> vertices;
	private ArrayList<Vertex> verticesList;

	public Graph() {
		vertices = new HashMap<Integer, Vertex>();
		verticesList = new ArrayList<Vertex>();
	}

	public void addConstraint(int var1, int var2) {
		Vertex v1 = new Vertex(var1);
		Vertex v2 = new Vertex(var2);

		if (!vertices.containsKey(v1.hashCode())) {
			vertices.put(v1.hashCode(), v1);
			verticesList.add(v1);
		}

		if (!vertices.containsKey(v2.hashCode())) {
			vertices.put(v2.hashCode(), v2);
			verticesList.add(v2);
		}

		vertices.get(v1.hashCode()).addRelation(vertices.get(v2.hashCode()));
		vertices.get(v2.hashCode()).addRelation(vertices.get(v1.hashCode()));
	}

	/**
	 * @return the verticesList
	 */
	public ArrayList<Vertex> getVerticesList() {
		return verticesList;
	}

}
