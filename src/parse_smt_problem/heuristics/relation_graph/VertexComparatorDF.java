package parse_smt_problem.heuristics.relation_graph;

import java.util.Comparator;

public class VertexComparatorDF implements Comparator<Vertex> {
	private int maximize;

	public VertexComparatorDF(boolean maximize) {
		this.maximize = maximize ? 1 : -1;
	}

	@Override
	public int compare(Vertex arg0, Vertex arg1) {
		float nbRelationsArg0 = arg0.getCoM();
		float nbRelationsArg1 = arg1.getCoM();

		if (nbRelationsArg0 > nbRelationsArg1)
			return 1 * maximize;

		if (nbRelationsArg0 < nbRelationsArg1)
			return -1 * maximize;

		return 0;
	}
}
