package ddd_datastructure.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ddd_datastructure.DDDFactory;
import ddd_datastructure.LogicalOperator;
import ddd_datastructure.NonTerminal;
import ddd_datastructure.DDDVertex;

public class Path implements Cloneable {
	private HashMap<Integer, Integer> variablesMap;
	private ArrayList<Edge> A;
	private float[][] dist;

	public Path() {
		A = new ArrayList<Edge>();
		variablesMap = new HashMap<Integer, Integer>();
		dist = null;
	}

	/**
	 * 
	 * @param v
	 * @param high
	 */
	public void addNode(NonTerminal v, boolean high) {
		if (!variablesMap.containsKey(v.getPos())) {
			// add the node represented by the positive variable
			variablesMap.put(v.getPos(), variablesMap.size());
		}

		if (!variablesMap.containsKey(v.getNeg())) {
			// add the node represented by the negative variable
			variablesMap.put(v.getNeg(), variablesMap.size());
		}

		// add the node that represent our arc
		Edge arc = new Edge(v, high);
		A.add(arc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Path p = (Path) super.clone();

		p.variablesMap = (HashMap<Integer, Integer>) variablesMap.clone();
		p.A = (ArrayList<Edge>) A.clone();

		return p;
	}

	/**
	 * use the Floyd–Warshall algorithm to find if the current path is feasible
	 * 
	 * @return true if the current path is feasible
	 */
	public boolean feasible() {
		ArrayList<Edge> negativeEdges = new ArrayList<Edge>();
		// Initialization
		dist = new float[variablesMap.size()][variablesMap.size()];

		for (int i = 0; i < variablesMap.size(); i++) {
			Arrays.fill(dist[i], Float.POSITIVE_INFINITY);
			dist[i][i] = 0;
		}

		// the weight of the edges
		for (Edge edge : A) {
			dist[variablesMap.get(edge.getNeg())][variablesMap.get(edge
					.getPos())] = edge.getConstant();
			if (edge.opIsStrict())
				negativeEdges.add(edge);
		}

		// Floyd Warshall
		for (int k = 0; k < variablesMap.size(); k++) {
			for (int i = 0; i < variablesMap.size(); i++) {
				for (int j = 0; j < variablesMap.size(); j++) {
					if (dist[i][j] > dist[i][k] + dist[k][j])
						dist[i][j] = dist[i][k] + dist[k][j];
				}

			}

		}

		// check for negative cycles
		for (int i = 0; i < variablesMap.size(); i++) {
			if (dist[i][i] < 0)
				return false;

		}

		// check on inequalities constraints
		for (Edge edge : negativeEdges) {
			int v = variablesMap.get(edge.getNeg());
			int w = variablesMap.get(edge.getPos());
			if (dist[v][w] == -dist[w][v])
				return false;
		}

		return true;
	}

	public DDDVertex toTightDDD(DDDFactory factory) {
		if (!this.feasible()) {
			System.err.println("Should never append!!");
			System.exit(1);
		}

		int negVarIndice = variablesMap.get(A.get(0).getNeg());
		int posVarIndice = variablesMap.get(A.get(0).getPos());
		DDDVertex result = A.get(0).toDDDVertex(factory,
				dist[negVarIndice][posVarIndice]);

		for (int i = 1; i < A.size(); i++) {
			negVarIndice = variablesMap.get(A.get(i).getNeg());
			posVarIndice = variablesMap.get(A.get(i).getPos());
			DDDVertex inter = A.get(i).toDDDVertex(factory,
					dist[negVarIndice][posVarIndice]);

			result = factory.APPLY(LogicalOperator.AND, result, inter);
		}

		return result;

	}
	
	public void lastToBot() {
		A.get(A.size()-1).setToBot();
	}
}
