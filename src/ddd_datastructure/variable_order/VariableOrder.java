package ddd_datastructure.variable_order;

import java.util.HashMap;

import ddd_datastructure.NonTerminal;

public class VariableOrder {
	HashMap<Integer, Integer> variableHashOrder;

	public VariableOrder() {
		variableHashOrder = new HashMap<Integer, Integer>();
	}

	/**
	 * Add variable as the last one in the Order if variableHashOrder does not
	 * contain it
	 * 
	 * @param variable
	 */
	public void addVariable(int variable) {
		if (!variableHashOrder.containsKey(variable))
			variableHashOrder.put(variable, variableHashOrder.size());

	}

	/**
	 * switch the order between variable1 and variable2
	 * 
	 * @param variable1
	 * @param variable2
	 */
	public void switchOrder(int variable1, int variable2) {
		int oldVariable1 = variableHashOrder.put(variable1,
				variableHashOrder.get(variable2));
		variableHashOrder.put(variable2, oldVariable1);
	}

	/**
	 * 
	 * @param variable1
	 * @param variable2
	 * @return true if variable1 is before variable2
	 */
	public boolean prec(int variable1, int variable2) {
		return variableHashOrder.get(variable1) < variableHashOrder
				.get(variable2);
	}

	/**
	 * (x, y) ≺ (x', y') iff y ≺ y' or y = y' and x ≺ x' where x and x' are the
	 * positive variables y and y' are the negative variables
	 * 
	 * @param v1
	 * @param v2
	 * @return true if the constraint of v2 is preceded by v1
	 */
	public boolean prec(NonTerminal v1, NonTerminal v2) {
		// rule: (xi, xj) < (x'i, x'j) iff:
		// xj < x'j or (xj = x'j and xi < x'i)
		boolean rule = prec(v1.getNeg(), v2.getNeg());
		rule = rule
				|| (v1.getNeg() == v2.getNeg() && prec(v1.getPos(), v2.getPos()));

		// (var(v) = var(low(v)) AND bound(v) < bound(low(v)))
		rule = rule || (v1.hasTheSameVariables(v2) && v1.precBound(v2));
		return rule;
	}

	/**
	 * verify the order between pos(v) and neg(v)
	 * 
	 * @param v
	 * @return true if it is correct
	 */
	public boolean localVerify(NonTerminal v) {
		// rule1: neg(v) < pos(v)
		boolean rule1 = prec(v.getNeg(), v.getPos());
		// rule2: var(v) < var(high(v))
		boolean rule2 = true;
		if (v.getHigh() instanceof NonTerminal)
			rule2 = prec(v, (NonTerminal) v.getHigh());

		// rule3: (var(v) < var(low(v))) OR
		// (var(v) = var(low(v)) AND bound(v) < bound(low(v)))
		boolean rule3 = true;
		if (v.getLow() instanceof NonTerminal) {
			NonTerminal low = (NonTerminal) v.getLow();
			rule3 = prec(v, low)
					|| (v.hasTheSameVariables(low) && v.precBound(low));
		}

		return rule1 && rule2 && rule3;
	}

	public int getOrder(int var) {
		return variableHashOrder.get(var);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VariableOrder [variableHashOrder=" + variableHashOrder + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((variableHashOrder == null) ? 0 : variableHashOrder
						.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableOrder))
			return false;
		VariableOrder other = (VariableOrder) obj;
		if (variableHashOrder == null) {
			if (other.variableHashOrder != null)
				return false;
		} else if (!variableHashOrder.equals(other.variableHashOrder))
			return false;

		return true;
	}

}
