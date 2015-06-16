package parse_smt_problem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import parse_smt_problem.heuristics.HeuristicOrder;
import ddd_datastructure.ComparativeOperator;
import ddd_datastructure.DDDFactory;
import ddd_datastructure.DDDVertex;
import ddd_datastructure.LogicalOperator;
import ddd_datastructure.variable_order.VariableOrder;

public class ParseProblem {
	private HashMap<String, Integer> mapVariables;
	private VariableOrder order;
	private ddd_datastructure.DDDVertex ddd;
	private ddd_datastructure.DDDFactory dddFactory;
	private String filename;
	private boolean locallyReduced, pathReduced;

	public ParseProblem(String filename, HeuristicOrder heuristic) {
		mapVariables = new HashMap<String, Integer>();
		ddd = null;

		chargeVariables(filename, heuristic);
		dddFactory = new DDDFactory(order);
		this.filename = filename;
		locallyReduced = false;
		pathReduced = false;
	}

	private DDDVertex parseFormula(LogicalOperator operator,
			BufferedReader bufferReader, ArrayList<String> lineSplitted,
			Iterator<String> iterator) {
		DDDVertex vertex = null;
		try {
			// it is not a daemon! It compute a subset of the formula
			while (true) {
				int parenthesisOpen = 0;

				int pos = -1;
				int neg = -1;
				boolean posDefined = false;
				String operatorOfConstraint = null;
				float constant = 0;

				while (iterator.hasNext()) {
					String string = iterator.next();
					if (string.equals("(")) {
						++parenthesisOpen;
						continue;
					}

					if (string.equals("or") || string.equals("and")) {
						--parenthesisOpen;
						DDDVertex newVertex = null;
						if (string.equals("or"))
							newVertex = parseFormula(LogicalOperator.OR,
									bufferReader, lineSplitted, iterator);
						else
							newVertex = parseFormula(LogicalOperator.AND,
									bufferReader, lineSplitted, iterator);

						if (newVertex == null) {
							System.err
									.println("Should not append, maybe the file is not conform");
							System.out.println("ParseProblem2.parseFormula()");
							System.exit(1);
						}

						// add the new vertex to our current ddd
						if (vertex == null)
							vertex = newVertex;
						else
							vertex = dddFactory.APPLY(operator, vertex,
									newVertex);

						continue;
					}

					// define the operator; "<=", "<", "=" ...
					if (supportedOperator(string)) {
						operatorOfConstraint = string;
						if (string.equals("=")) {
							// FIXME correct it to be aware with constraints
							// like (= St_spy_variable (+ 1 t_Init_0))
							System.err.println("Operator not yet supported");
							System.out.println("operator: \""
									+ operatorOfConstraint + "\"");
							break;
						}

						continue;
					}

					if (mapVariables.containsKey(string)) {
						int var = mapVariables.get(string);
						if (!posDefined) {
							posDefined = true;
							pos = var;
						} else {
							neg = var;
						}

						continue;
					}

					if (!posDefined && string.equals("-")) {
						// here assume the constraint is like
						// (< (- posVariable negVariable) ...
						continue;
					}

					if (string.equals(")")) {
						--parenthesisOpen;
						if (parenthesisOpen == 0) {
							// time to construct our new vertex
							DDDVertex newVertex = createVertex(pos, neg,
									operatorOfConstraint, constant);
							if (newVertex != null)
								if (vertex == null)
									vertex = newVertex;
								else
									vertex = dddFactory.APPLY(operator, vertex,
											newVertex);

							// reset to initial value variables
							pos = -1;
							neg = -1;
							posDefined = false;
							operatorOfConstraint = null;
							constant = 0;
						}

						if (parenthesisOpen < 0)
							return vertex;

						continue;
					}

					// here we assume the pos and neg variables are defined and
					// the only thing to do is to define the constant
					if (parenthesisOpen > 1) {
						// the constraint is like
						// (< (- a b) (+ number1 number2))

						float number1 = Float.parseFloat(iterator.next());
						float number2 = Float.parseFloat(iterator.next());
						switch (string) {
						case "+":
							constant = number1 + number2;
							break;

						case "-":
							constant = number1 - number2;
							break;

						case "/":
							constant = number1 / number2;
							break;

						case "*":
							constant = number1 * number2;
							break;

						default:
							// operator not supported
							System.out.println("ParseProblem2.parseFormula()");
							System.err.println("Operator " + string
									+ " is not supported");
							System.exit(1);
							break;
						}

					} else {
						// the constraint is like (< (- a b) constant)
						if (!string.isEmpty()) {
							if (string.equals("true")) {
								if (operator == LogicalOperator.AND) {
									// nothing to do
								} else {
									System.err
											.println("true values in an or is not yet implemented");
									System.exit(1);
								}

							} else {
								constant = Float.parseFloat(string);
							}

						}

					}

				}

				String line = bufferReader.readLine();
				if (line == null)
					break;

				line = line.replaceAll("\\(", " \\( ");
				line = line.replaceAll("\\)", " \\) ");
				line = line.replaceAll("  ", " ");
				lineSplitted = new ArrayList<String>(Arrays.asList(line
						.split(" ")));
				iterator = lineSplitted.iterator();
			}

		} catch (IOException e) {
			System.out.println("ParseProblem2.parseFormula()");
			System.err.println();
			e.printStackTrace();
		}

		System.out.println("ParseProblem2.parseFormula()");
		return null;
	}

	/**
	 * charge the DDD from the file require an order
	 * 
	 * @param filename
	 */
	private void defineDDDLocallyReduced(String filename) {
		BufferedReader bufferReader = null;

		try {
			bufferReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Error file: " + filename + " was not found");
			System.exit(1);
		}

		try {
			String line = bufferReader.readLine();
			while (line != null
					&& (!line.contains(":formula") && !line.contains("(assert"))) {
				line = bufferReader.readLine();
			}

			LogicalOperator operator = null;
			line = line.replaceFirst("\\(assert \\(|\\:formula \\( ", "");
			// here the formula parse is begin
			if (line.matches("or.*")) {
				operator = LogicalOperator.OR;
				line = line.replaceFirst("or |or", "");
			} else {
				if (line.matches("and.*")) {
					operator = LogicalOperator.AND;
					line = line.replaceFirst("and |and", "");
				} else {
					System.err.println("First operator not supported");
					System.out.println("ParseProblem2.defineDDD()");
					System.out.println(line);
					System.exit(1);
				}
			}

			line = line.replaceAll("\\(", " \\( ");
			line = line.replaceAll("\\)", " \\) ");
			line = line.replaceAll("  ", " ");
			ArrayList<String> lineSplitted = new ArrayList<String>(
					Arrays.asList(line.split(" ")));

			ddd = parseFormula(operator, bufferReader, lineSplitted,
					lineSplitted.iterator());

		} catch (IOException e) {
			System.err.println("Error while reading the file: " + filename);
			e.printStackTrace();
		}

		try {
			bufferReader.close();
		} catch (IOException e) {
			System.err.println("Error when closing " + filename);
			e.printStackTrace();
		}
	}

	private boolean supportedOperator(String line) {
		return line.contains("<") || line.contains("<=") || line.contains(">")
				|| line.contains(">=") || line.contains("=");
	}

	/**
	 * generate the vertex that follow the order
	 * 
	 * @param pos
	 * @param neg
	 * @param operator
	 * @param constant
	 * @return
	 */
	private DDDVertex createVertex(int pos, int neg, String operator,
			float constant) {
		ddd_datastructure.DDDVertex newVertex = null;

		if (order.prec(neg, pos)) {
			if (operator.equals("<"))
				newVertex = dddFactory.MK(pos, neg, ComparativeOperator.LT,
						constant, dddFactory.getZero(), dddFactory.getOne());

			if (operator.equals("<="))
				newVertex = dddFactory.MK(pos, neg, ComparativeOperator.LEQ,
						constant, dddFactory.getZero(), dddFactory.getOne());

			if (operator.equals(">"))
				newVertex = dddFactory.MK(pos, neg, ComparativeOperator.LEQ,
						constant, dddFactory.getOne(), dddFactory.getZero());

			if (operator.equals(">="))
				newVertex = dddFactory.MK(pos, neg, ComparativeOperator.LT,
						constant, dddFactory.getOne(), dddFactory.getZero());

		} else {
			if (operator.equals("<"))
				newVertex = dddFactory.MK(neg, pos, ComparativeOperator.LEQ,
						-constant, dddFactory.getOne(), dddFactory.getZero());

			if (operator.equals("<="))
				newVertex = dddFactory.MK(neg, pos, ComparativeOperator.LT,
						-constant, dddFactory.getOne(), dddFactory.getZero());

			if (operator.equals(">"))
				newVertex = dddFactory.MK(neg, pos, ComparativeOperator.LT,
						-constant, dddFactory.getZero(), dddFactory.getOne());

			if (operator.equals(">="))
				newVertex = dddFactory.MK(neg, pos, ComparativeOperator.LEQ,
						-constant, dddFactory.getZero(), dddFactory.getOne());
		}

		if (newVertex == null) {
			System.err.println("operator: [" + operator + "]");
			System.out.println("ParseProblem.addConstraint()");
			System.exit(1);
		}

		return newVertex;
	}

	/**
	 * 
	 * @param filename
	 */
	private void chargeVariables(String filename, HeuristicOrder heuristic) {
		BufferedReader bufferReader = null;

		try {
			bufferReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Error file: " + filename + " was not found");
			System.exit(1);
		}

		try {
			String line = bufferReader.readLine();
			while (line != null) {
				// associate a number to every variables
				if (line.contains("extrafuns") || line.contains("declare-fun")) {
					line = line.replaceAll("\\(", "");
					String[] lineSplited = line.split(" ");
					mapVariables.put(lineSplited[1], mapVariables.size());

					line = bufferReader.readLine();
					continue;
				}

				// parse the formula
				if (line.matches("\\:formula.*|\\(assert.*")) {
					order = heuristic
							.getOrder(line, bufferReader, mapVariables);
					break;
				}

				line = bufferReader.readLine();
			}

		} catch (IOException e) {
			System.err.println("Error while reading the file: " + filename);
			e.printStackTrace();
		}

		try {
			bufferReader.close();
		} catch (IOException e) {
			System.err.println("Error when closing " + filename);
			e.printStackTrace();
		}

	}

	/**
	 * @return the ddd, which is locally reduced
	 */
	public ddd_datastructure.DDDVertex computeDDDLocallyReduced() {
		defineDDDLocallyReduced(filename);
		locallyReduced = true;
		return ddd;
	}

	/**
	 * require computeDDDLocallyReduced() call first
	 * 
	 * @return the ddd, which is path reduced
	 */
	public ddd_datastructure.DDDVertex computeDDDPathReduced() {
		if (!locallyReduced)
			computeDDDLocallyReduced();

		pathReduced = true;
		return ddd = dddFactory.pathReduce(ddd);
	}

	/**
	 * require computeDDDTight() call first
	 * 
	 * @return the ddd, which is tight
	 */
	public ddd_datastructure.DDDVertex computeDDDTight() {
		if (!pathReduced)
			computeDDDPathReduced();

		return ddd = dddFactory.tight(ddd);
	}

	/**
	 * 
	 * @return the order used to construct the ddd
	 */
	public String orderToString() {
		return order.toString();
	}

	/**
	 * 
	 * @return the factory used
	 */
	public ddd_datastructure.DDDFactory getDddFactory() {
		return dddFactory;
	}
}
