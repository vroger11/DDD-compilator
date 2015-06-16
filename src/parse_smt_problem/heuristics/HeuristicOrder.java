package parse_smt_problem.heuristics;

import java.io.BufferedReader;
import java.util.HashMap;

import ddd_datastructure.variable_order.VariableOrder;

public interface HeuristicOrder {

	/**
	 * 
	 * @param firstLine
	 *            contains the string ":formula"
	 * @param br
	 * @param mapVariables
	 * @return
	 */
	public VariableOrder getOrder(String firstLine, BufferedReader br,
			HashMap<String, Integer> mapVariables);
}
