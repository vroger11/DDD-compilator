package ddd_datastructure.path;

import ddd_datastructure.ComparativeOperator;
import ddd_datastructure.DDDFactory;
import ddd_datastructure.DDDVertex;
import ddd_datastructure.NonTerminal;

public class Edge {
	private int pos;
	private int neg;
	private ComparativeOperator op;
	private float constant;

	private boolean inverted; // positive and negative variables were inverted

	public Edge(NonTerminal ntv, boolean high) {
		if (inverted = !high) {
			pos = ntv.getNeg();
			neg = ntv.getPos();
			op = (op == ComparativeOperator.LEQ) ? ComparativeOperator.LT
					: ComparativeOperator.LEQ;
			constant = -ntv.getConstant();
		} else {
			pos = ntv.getPos();
			neg = ntv.getNeg();
			op = ntv.getOp();
			constant = ntv.getConstant();
		}

	}

	public Edge(int pos, int neg, float constant) {
		this.pos = pos;
		this.neg = neg;
		this.constant = constant;
		op = ComparativeOperator.LEQ;
	}

	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @return the neg
	 */
	public int getNeg() {
		return neg;
	}

	/**
	 * @return the op
	 */
	public ComparativeOperator getOp() {
		return op;
	}

	public boolean opIsStrict() {
		return op == ComparativeOperator.LT;
	}

	/**
	 * @return the constant
	 */
	public float getConstant() {
		return constant;
	}

	/**
	 * @return the inverted
	 */
	public boolean isInverted() {
		return inverted;
	}

	DDDVertex toDDDVertex(DDDFactory factory, float constant) {
		DDDVertex result;

		if (inverted) {
			//TODO check if it is correct
			result = factory.MK(neg, pos,
					(op == ComparativeOperator.LT) ? ComparativeOperator.LEQ
							: ComparativeOperator.LT, -constant, factory
							.getOne(), factory.getZero());
		} else {
			result = factory.MK(pos, neg, op, constant, factory.getZero(),
					factory.getOne());
		}

		return result;
	}
}
