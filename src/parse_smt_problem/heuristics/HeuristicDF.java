package parse_smt_problem.heuristics;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map.Entry;

import parse_smt_problem.heuristics.relation_graph.Vertex;
import parse_smt_problem.heuristics.relation_graph.VertexComparatorDF;
import ddd_datastructure.variable_order.VariableOrder;

public class HeuristicDF extends HeuristicCFD {
	private final int maxIteration = 5000;
	private boolean maximize;

	public HeuristicDF(boolean maximize) {
		super(maximize);
		this.maximize = maximize;
	}

	@Override
	public VariableOrder getOrder(String firstLine, BufferedReader br,
			HashMap<String, Integer> mapVariables) {
		// initialize the graph
		parseToGraph(firstLine, br, mapVariables);

		// first initialize
		for (parse_smt_problem.heuristics.relation_graph.Vertex vertex : graph
				.getVerticesList()) {
			order.addVariable(vertex.getVarNumber());
		}

		VariableOrder orderNext = order;

		int count = 0;
		do {
			order = orderNext;
			computeCoM();

			graph.getVerticesList().sort(new VertexComparatorDF(maximize));
			orderNext = new VariableOrder();
			for (Vertex vertex : graph.getVerticesList()) {
				orderNext.addVariable(vertex.getVarNumber());
			}

			++count;
		} while (!order.equals(orderNext) && count < maxIteration);

		return order;
	}

	/**
	 * compute the CoM of every vertices
	 */
	private void computeCoM() {
		for (Vertex vertex : graph.getVerticesList()) {
			for (Entry<Vertex, Float> entry : vertex.getLinked().entrySet()) {

				float result = (order.getOrder(vertex.getVarNumber()) + order
						.getOrder(entry.getKey().getVarNumber())) / 2;
				entry.setValue(result);
				vertex.setCoM(vertex.getCoM() + result);
			}

			// normalize
			vertex.setCoM(vertex.getCoM() / vertex.getLinked().size());
		}

	}

}
