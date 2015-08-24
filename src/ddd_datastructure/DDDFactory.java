package ddd_datastructure;

import java.util.ArrayList;
import java.util.HashMap;

import ddd_datastructure.path.Path;
import ddd_datastructure.variable_order.VariableOrder;

public class DDDFactory {
	private HashMap<Integer, DDDVertex> hashVertices;
	private VariableOrder variableOrder;
	private Terminal zero;
	private Terminal one;

	/**
	 * default constructor, the order between variables will be define by the
	 * order of appearance
	 */
	public DDDFactory() {
		hashVertices = new HashMap<Integer, DDDVertex>();
		variableOrder = new VariableOrder();

		createTermialVertices();
	}

	/**
	 * use the order given in parameter
	 * 
	 * @param order
	 *            on some variables the DDD has to follow
	 */
	public DDDFactory(VariableOrder order) {
		hashVertices = new HashMap<Integer, DDDVertex>();
		variableOrder = order;

		createTermialVertices();
	}

	/**
	 * function use to create all new nodes
	 * 
	 * @param pos
	 * @param neg
	 * @param op
	 * @param constant
	 * @param low
	 * @param high
	 * 
	 * @return a vertex that represent a ddd locally reduce
	 */
	public DDDVertex MK(int pos, int neg, ComparativeOperator op,
			float constant, DDDVertex low, DDDVertex high) {
		if (low == null || high == null) {
			System.err.println("high or low is null");
			System.out.println("DDDFactory.MK()");
			return null;
		}

		// the locally reduced
		int currentCode = NonTerminal.computeHashKey(pos, neg, op, constant,
				low, high);

		// rule 1: D = Z implies op(v) = LEQ,
		// we have float so it is unnecessary to implement

		// rule 2: attr(u) = attr(v) implies u=v
		if (hashVertices.containsKey(currentCode))
			return hashVertices.get(currentCode);

		// rule 3 low(v) != high(v)
		if (low.equals(high)) {
			// the current vertex will become the low
			return low;
		}

		NonTerminal newVertex = new NonTerminal(pos, neg, op, constant, low,
				high, currentCode);
		// rule 4: (var(v) = var(low(v))) implies (high(v) != high(low(v))
		if (low instanceof NonTerminal) {

			if (newVertex.hasTheSameVariables((NonTerminal) low)) {
				if (high.equals(((NonTerminal) low).getHigh())) {
					return low;
				}

			}

		}

		// trace: control order
		//		variableOrder.addVariable(neg);
		//		variableOrder.addVariable(pos);
		//		if (!variableOrder.localVerify(newVertex)) {
		//			System.err.println("problem with the order");
		//			System.err.println("DDDFactory.MK()");
		//
		//			// the following code is for debugging
		//			System.err.println(variableOrder);
		//			System.err.println("Pos " + newVertex.getPos() + " -> "
		//					+ variableOrder.getOrder(newVertex.getPos()) + " Neg "
		//					+ newVertex.getNeg() + " -> "
		//					+ variableOrder.getOrder(newVertex.getNeg()));
		//			System.err.println();
		//			if (low instanceof NonTerminal) {
		//				NonTerminal lowNT = (NonTerminal) newVertex.getLow();
		//				System.err.println("Low: Pos " + lowNT.getPos() + " -> "
		//						+ variableOrder.getOrder(lowNT.getPos()) + " Neg "
		//						+ lowNT.getNeg() + " -> "
		//						+ variableOrder.getOrder(lowNT.getNeg()));
		//			}
		//
		//			if (high instanceof NonTerminal) {
		//				NonTerminal highNT = (NonTerminal) newVertex.getHigh();
		//				System.err.println("High: Pos " + highNT.getPos() + " -> "
		//						+ variableOrder.getOrder(highNT.getPos()) + " Neg "
		//						+ highNT.getNeg() + " -> "
		//						+ variableOrder.getOrder(highNT.getNeg()));
		//			}
		//
		//			// print the stack trace
		//			Exception ex = new Exception();
		//			ex.printStackTrace();
		//
		//			System.exit(1);
		//		}

		hashVertices.put(currentCode, newVertex);
		return newVertex;
	}

	/**
	 * @return the zero vertex
	 */
	public Terminal getZero() {
		return zero;
	}

	/**
	 * @return the one vertex
	 */
	public Terminal getOne() {
		return one;
	}

	/**
	 * Extended apply of bryant
	 * 
	 * @return
	 */
	public DDDVertex APPLY(LogicalOperator op, DDDVertex v1, DDDVertex v2) {
		HashMap<KeyVertexVertex, DDDVertex> T = new HashMap<KeyVertexVertex, DDDVertex>();
		DDDVertex u = applyStep(op, v1, v2, T);
		return u;
	}

	/**
	 * 
	 * @param v
	 *            the vertex that represent a ddd
	 * @return a path reduced ddd
	 */
	public DDDVertex pathReduce(DDDVertex v) {
		return recursivePathReduce(v, new Path());
	}

	/**
	 * suppose the ddd represented by v is already path-reduced
	 * 
	 * @param v
	 * @return
	 */
	public DDDVertex tight(DDDVertex v) {
		ArrayList<Path> paths = new ArrayList<Path>();
		recursiveSearchOfPaths(paths, v, new Path());

		if (paths.size() == 0)
			// it is equals to 0 when v is a terminal vertex
			return v;

		DDDVertex tightDDD = paths.get(0).toTightDDD(this);
		for (int i = 1; i < paths.size(); i++) {
			DDDVertex nextTight = paths.get(i).toTightDDD(this);
			tightDDD = APPLY(LogicalOperator.OR, tightDDD, nextTight);
		}

		return tightDDD;
	}

	/**
	 * enumerate every path
	 * 
	 * @param paths
	 * @param v
	 * @param current
	 */
	private void recursiveSearchOfPaths(ArrayList<Path> paths,
			DDDVertex v, Path current) {
		if (v.equals(getOne()))
			paths.add(current);

//		if (v.equals(getZero())) {
//			current.lastToBot();
//			paths.add(current);
//		}
			
			
		if (v instanceof NonTerminal) {
			NonTerminal ntv = (NonTerminal) v;
			Path currentLow = null;
			try {
				currentLow = (Path) current.clone();
			} catch (CloneNotSupportedException e) {
				System.out
						.println("DDDFactory.recursiveSearchOfPositivePaths()");
				e.printStackTrace();
				System.exit(1);
			}

			current.addNode(ntv, true);
			currentLow.addNode(ntv, false);
			recursiveSearchOfPaths(paths, ntv.getHigh(),
					current);
			recursiveSearchOfPaths(paths, ntv.getLow(),
					currentLow);
		}

	}

	/**
	 * sub-function describe by Bryant and adapted for DDD (following Moller
	 * indications)
	 * 
	 * @param op
	 * @param v1
	 * @param v2
	 * @param T
	 * @return
	 */
	private DDDVertex applyStep(LogicalOperator op, DDDVertex v1, DDDVertex v2,
			HashMap<KeyVertexVertex, DDDVertex> T) {

		KeyVertexVertex key = new KeyVertexVertex(v1.hashCode(), v2.hashCode());
		// FIXME the key is not accurate enough for large graphs
		// and can generate bugs on graph with many vertices
		// maybe we should not use hashCode of the vertices
		if (T.containsKey(key)) {
			DDDVertex v = T.get(key);

			return v;
		}

		DDDVertex solution = applyTrivialSolution(op, v1, v2);
		if (solution != null) {
			T.put(key, solution);
			return solution;
		}

		// Create a nonterminal vertex for non trivial solution
		DDDVertex u;
		NonTerminal tv1 = (NonTerminal) v1;
		NonTerminal tv2 = (NonTerminal) v2;

		// tv1 == (α → h, l) and tv2 = (α' → h', l')
		// (α → h, l) op (α' → h', l')
		if (tv1.hasTheSameVariables(tv2)) {
			// we can simplify
			if (tv1.hasTheSameCstr(tv2)) // α = α'
				u = MK(tv1.getPos(), tv1.getNeg(), tv1.getOp(),
						tv1.getConstant(),
						applyStep(op, tv1.getLow(), tv2.getLow(), T),
						applyStep(op, tv1.getHigh(), tv2.getHigh(), T));
			else {
				if (variableOrder.prec(tv1, tv2)) // α < α'
					u = MK(tv1.getPos(), tv1.getNeg(), tv1.getOp(),
							tv1.getConstant(),
							applyStep(op, tv1.getLow(), tv2, T),
							applyStep(op, tv1.getHigh(), tv2.getHigh(), T));
				else
					// α > α'
					u = MK(tv2.getPos(), tv2.getNeg(), tv2.getOp(),
							tv2.getConstant(),
							applyStep(op, tv1, tv2.getLow(), T),
							applyStep(op, tv1.getHigh(), tv2.getHigh(), T));
			}

		} else {
			if (variableOrder.prec(tv1, tv2)) {
				// α → (h op (α' → h', l')), (l op (α' → h', l'))
				u = MK(tv1.getPos(), tv1.getNeg(), tv1.getOp(),
						tv1.getConstant(), applyStep(op, tv1.getLow(), tv2, T),
						applyStep(op, tv1.getHigh(), tv2, T));
			} else {
				// α' → (h' op (α → h, l)), (l' op (α → h , l))
				u = MK(tv2.getPos(), tv2.getNeg(), tv2.getOp(),
						tv2.getConstant(), applyStep(op, tv2.getLow(), tv1, T),
						applyStep(op, tv2.getHigh(), tv1, T));
			}

		}

		T.put(key, u);
		return u;
	}

	/**
	 * 
	 * @return a string that represent the order of variable
	 */
	public String orderToString() {
		return variableOrder.toString();
	}

	/**
	 * 
	 * @param op
	 * @param v1
	 * @param v2
	 * 
	 * @return null if there is no trivial solution
	 */
	private DDDVertex applyTrivialSolution(LogicalOperator op, DDDVertex v1,
			DDDVertex v2) {
		switch (op) {
		case AND:
			if (v1 instanceof Terminal && !v1.fRepresentation(null)
					|| v2 instanceof Terminal && !v2.fRepresentation(null))
				return this.getZero();

			if (v1 instanceof Terminal && v2 instanceof Terminal)
				return this.getOne();

			if (v1 instanceof Terminal)
				return v2;

			if (v2 instanceof Terminal)
				return v1;

			break;

		case OR:
			if (v1 instanceof Terminal && v1.fRepresentation(null)
					|| v2 instanceof Terminal && v2.fRepresentation(null))
				return this.getOne();

			if (v1 instanceof Terminal && v2 instanceof Terminal)
				return this.getZero();

			if (v1 instanceof Terminal)
				return v2;

			if (v2 instanceof Terminal)
				return v1;
			break;

		// TODO add more operators

		default:
			System.err.println("operator" + op.toString() + "not implemented");
			break;
		}

		return null;
	}

	/**
	 * create terminal vertices 0 and 1 that are necessary to create every nodes
	 */
	private void createTermialVertices() {
		zero = new Terminal(false);
		one = new Terminal(true);
		hashVertices.put(zero.hashCode(), zero);
		hashVertices.put(zero.hashCode(), one);
	}

	/**
	 * 
	 * @param v
	 * @param p
	 * @return a ddd path reduced
	 */
	private DDDVertex recursivePathReduce(DDDVertex v, Path p) {
		if (!p.feasible())
			return null;

		if (v.equals(getOne()) || v.equals(getZero()))
			return v;

		// there we are sure v is a non terminal vertex
		NonTerminal ntv = (NonTerminal) v;
		Path p2 = null;
		try {
			p2 = (Path) p.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("DDDFactory.recursivePathReduce()");
			e.printStackTrace();
			System.exit(1);
		}

		p.addNode(ntv, true);
		p2.addNode(ntv, false);
		DDDVertex h = recursivePathReduce(ntv.getHigh(), p);
		DDDVertex l = recursivePathReduce(ntv.getLow(), p2);
		if (l != null && h != null)
			return MK(ntv.getPos(), ntv.getNeg(), ntv.getOp(),
					ntv.getConstant(), l, h);

		if (h != null)
			return h;
		else
			return l;

	}

}
