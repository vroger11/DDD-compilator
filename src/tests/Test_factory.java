package tests;

import ddd_datastructure.ComparativeOperator;
import ddd_datastructure.DDDFactory;
import ddd_datastructure.LogicalOperator;
import ddd_datastructure.DDDVertex;

public class Test_factory {

	public static void main(String[] args) {
		DDDFactory factory = new DDDFactory();

		DDDVertex u = factory.MK(1, 2, ComparativeOperator.LEQ, 20,
				factory.getOne(), factory.getOne());
		if (u != factory.getOne()) {
			System.err.println("Error, not the result expected");
			System.exit(1);
		}

		// test creation of vertices
		u = factory.MK(2, 1, ComparativeOperator.LEQ, 20, factory.getOne(),
				factory.getOne());
		DDDVertex v = factory.MK(3, 2, ComparativeOperator.LEQ, 10,
				factory.getOne(), factory.getOne());
		DDDVertex f = factory.APPLY(LogicalOperator.AND, u, v);
		if (f == null) {
			System.err.println("f should not be equal to null");
			System.exit(1);
		}

		DDDVertex v1 = factory.MK(1, 2, ComparativeOperator.LT, 0,
				factory.getOne(), factory.getZero());
		DDDVertex v2 = factory.MK(1, 2, ComparativeOperator.LEQ, 0,
				factory.getZero(), factory.getOne());
		DDDVertex vEquality = factory.APPLY(LogicalOperator.AND, v1, v2);

		// test frepresentation
		float[] chi = { 3, 4, 5 };
		if (vEquality.fRepresentation(chi)) {
			System.err
					.println("Error, the fRepresentation should return false");
			System.out.println(vEquality.toString());
			System.exit(2);
		}

		float[] chi2 = { 3, 4, 4 };
		if (!vEquality.fRepresentation(chi2)) {
			System.err.println("Error, the fRepresentation should return true");
			System.out.println(vEquality.toString());
			System.exit(2);
		}

		// test pathreduce
		DDDVertex reduced = factory.pathReduce(vEquality); // this one should
															// not
															// be changing
		System.out.println("DOT bigraph 1 reduced:");
		System.out.println(reduced.generateDot());
		System.out.println();

		// test 2 pathReduce
		DDDVertex v3 = factory.MK(4, 3, ComparativeOperator.LEQ, -2,
				factory.getZero(), factory.getOne());
		DDDVertex v4 = factory.MK(5, 4, ComparativeOperator.LEQ, -2,
				factory.getZero(), factory.getOne());
		DDDVertex v5 = factory.MK(5, 3, ComparativeOperator.LEQ, 0,
				factory.getOne(), factory.getZero());

		DDDVertex nonReduced = factory.APPLY(LogicalOperator.AND,
				factory.APPLY(LogicalOperator.AND, v3, v4), v5);
		System.out.println("DOT bigraph 2 before reduced:");
		System.out.println(nonReduced.generateDot());
		System.out.println();

		System.out.println("number of vertices nonReduced1: "
				+ nonReduced.numberOfVertices());
		reduced = factory.pathReduce(nonReduced);
		System.out.println("number of vertices Reduced1: "
				+ nonReduced.numberOfVertices());

		System.out.println("DOT bigraph 2 reduced:");
		System.out.println(reduced.generateDot());
		System.out.println();

		// test outputs prints of ddd
		System.out.println("toString:");
		System.out.println(vEquality);
		System.out.println("DOT bigraph:");
		System.out.println(vEquality.generateDot());

		// test tight
		System.out.println();
		System.out.println("test Tight");

		v1 = factory.MK(10, 12, ComparativeOperator.LT, 0, factory.getOne(),
				factory.getZero());
		v2 = factory.MK(11, 12, ComparativeOperator.LT, 2, factory.getOne(),
				factory.getZero());
		v3 = factory.MK(11, 10, ComparativeOperator.LEQ, 0, factory.getZero(),
				factory.getOne());

		DDDVertex nonTight = factory.APPLY(LogicalOperator.AND,
				factory.APPLY(LogicalOperator.AND, v1, v2), v3);

		nonTight = factory.pathReduce(nonTight);
		System.out.println(nonTight.generateDot());
		DDDVertex tight = factory.tight(nonTight);
		System.out.println(tight.generateDot());
		System.out.println("Test pass");
	}
}
