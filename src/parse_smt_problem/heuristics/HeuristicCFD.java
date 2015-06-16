package parse_smt_problem.heuristics;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import parse_smt_problem.heuristics.relation_graph.Vertex;
import parse_smt_problem.heuristics.relation_graph.VertexComparatorCFD;
import ddd_datastructure.variable_order.VariableOrder;

public class HeuristicCFD implements HeuristicOrder {
	protected VariableOrder order;
	protected parse_smt_problem.heuristics.relation_graph.Graph graph;
	private boolean most;

	public HeuristicCFD(boolean most) {
		order = new VariableOrder();
		graph = new parse_smt_problem.heuristics.relation_graph.Graph();
		this.most = most;
	}

	@Override
	public VariableOrder getOrder(String firstLine, BufferedReader br,
			HashMap<String, Integer> mapVariables) {

		parseToGraph(firstLine, br, mapVariables);

		graph.getVerticesList().sort(new VertexComparatorCFD(most));

		for (Iterator<Vertex> iterator = graph.getVerticesList().iterator(); iterator
				.hasNext();) {
			Vertex vertex = (Vertex) iterator.next();
			order.addVariable(vertex.getVarNumber());
		}

		return order;
	}

	protected void parseToGraph(String firstLine, BufferedReader br,
			HashMap<String, Integer> mapVariables) {
		try {
			while (firstLine != null) {
				if (firstLine.contains("=") || firstLine.contains("<")
						|| firstLine.contains(">")) {
					firstLine = firstLine.replaceAll("\\)|\\(", "");
					String[] lineSplited = firstLine.split(" ");
					ArrayList<String> line = new ArrayList<String>(
							Arrays.asList(lineSplited));
					parseLine(line, mapVariables);
				}

				if (firstLine.equals("(check-sat)"))
					break;

				firstLine = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void parseLine(ArrayList<String> lineSplited,
			HashMap<String, Integer> mapVariables) {
		boolean addCurrent = false;

		for (Iterator<String> iterator = lineSplited.iterator(); iterator
				.hasNext();) {
			String string = iterator.next();

			// TODO support equals
			if (string.equals("<") || string.equals("<=") || string.equals(">")
					|| string.equals(">=")) {
				addCurrent = true;
				continue;
			}

			if (addCurrent && !string.equals("-")) {
				graph.addConstraint(mapVariables.get(string),
						mapVariables.get(iterator.next()));
				addCurrent = false;
			}

		}

	}
}
