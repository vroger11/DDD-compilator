package parse_smt_problem.heuristics.relation_graph;

import java.util.Comparator;

public class VertexComparatorCFD implements Comparator<Vertex> {
	private int ascending;

	public VertexComparatorCFD(boolean ascending) {
		this.ascending = ascending ? 1 : -1;
	}

	@Override
	public int compare(Vertex arg0, Vertex arg1) {
		int nbRelationsArg0 = arg0.numberRelations();
		int nbRelationsArg1 = arg1.numberRelations();

		if (nbRelationsArg0 > nbRelationsArg1)
			return 1 * ascending;

		if (nbRelationsArg0 < nbRelationsArg1)
			return -1 * ascending;

		return 0;
	}

}
