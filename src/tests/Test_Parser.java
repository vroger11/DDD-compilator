package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import ddd_datastructure.DDDVertex;
import parse_smt_problem.ParseProblem;
import parse_smt_problem.heuristics.HeuristicCFD;
import parse_smt_problem.heuristics.HeuristicDF;

public class Test_Parser {

	public static void main(String[] args) {
		if (args.length != 3) { // check the number of parameter
			String programName = System.getProperty("sun.java.command");
			String[] command = programName.split(" ");
			programName = command[0];

			System.err.println("wrong arguments");
			System.out
					.println("usage: "
							+ programName
							+ " <file to parse> <method number> <folder where the result will be>");
			System.exit(1);
		}

		parse_smt_problem.ParseProblem parser;
		int methodNumber = Integer.parseInt(args[1]);

		switch (methodNumber) {
		case 0:
			parser = new ParseProblem(args[0], new HeuristicCFD(false));
			break;

		case 1:
			parser = new ParseProblem(args[0], new HeuristicCFD(true));
			break;

		case 2:
			parser = new ParseProblem(args[0], new HeuristicDF(false));
			break;

		case 3:
			parser = new ParseProblem(args[0], new HeuristicDF(true));
			break;

		default:
			System.err.println("wrong number of method");
			System.out.println("available method: 0, 1, 2, or 3");
			parser = null;
			System.exit(1);
			break;
		}

		DDDVertex locallyReduced, pathReduced, tight = null;

		System.out.println(parser.orderToString());

		// locally Reduced
		long start = System.nanoTime();
		locallyReduced = parser.computeDDDLocallyReduced();
		long timeLocallyReduced = System.nanoTime() - start;
		System.out
				.println(timeLocallyReduced
						+ " nanoseconds, to be LocallyReduced since the order was defined");

		System.out.println("number of vertices: "
				+ locallyReduced.numberOfVertices());

		// path reduced
		start = System.nanoTime();
		pathReduced = parser.computeDDDPathReduced();
		long timePathReduced = System.nanoTime() - start;
		System.out
				.println(timePathReduced
						+ " nanoseconds, to be PathReduced since the DDD is locally reduced");
		System.out.println("number of vertices: "
				+ pathReduced.numberOfVertices());

		// tight
		start = System.nanoTime();
		tight = parser.computeDDDTight();
		long timeTight = System.nanoTime() - start;
		System.out.println(timeTight
				+ " nanoseconds, to be Tight since the DDD is path reduced");
		System.out.println("number of vertices: " + tight.numberOfVertices());

		FileWriter fstream;
		try {
			fstream = new FileWriter("graph-" + methodNumber + ".dot");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(tight.generateDot());
			out.close();
			fstream.close();
		} catch (IOException e1) {
			System.err.println("Error while writing on: " + "graph-"
					+ methodNumber + ".dot");
			e1.printStackTrace();
			System.exit(1);
		}

		System.out.println("launch dot");

		// generate folders if they are missing
		java.nio.file.Path path = Paths.get(args[0]);
		String fileName = path.getFileName().toString();
		File file = new File(args[2]);
		if (!file.exists())
			file.mkdir();

		String pathForTest = args[2] + "/" + fileName;
		file = new File(pathForTest);
		if (!file.exists())
			file.mkdir();

		// generate pdf for tight ddd
		// and add every informations in a file
		try {
			Process process = Runtime.getRuntime().exec(
					"dot -Tpdf graph-" + methodNumber + ".dot -o "
							+ pathForTest + "/graph-" + methodNumber + ".pdf");

			// write results in a file while dot process the pdf
			try {
				fstream = new FileWriter(pathForTest + "/graph-" + methodNumber
						+ ".txt");
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(timeLocallyReduced
						+ " nanoseconds, to LocallyReduced\n");

				out.write("number of vertices: "
						+ locallyReduced.numberOfVertices() + "\n");
				out.write(timePathReduced + " nanoseconds, to PathReduced\n");
				out.write("number of vertices: "
						+ pathReduced.numberOfVertices() + "\n");
				out.write(timeTight + " nanoseconds, to Tight\n");
				out.write("number of vertices: " + tight.numberOfVertices()
						+ "\n");
				out.write(parser.orderToString());

				out.close();
				fstream.close();
			} catch (IOException e1) {
				System.err.println("Error while writing on: " + "graph-"
						+ methodNumber + ".dot");
				e1.printStackTrace();
				System.exit(1);
			}

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				System.err.println("Error while waiting dot program");
				e.printStackTrace();
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Error while launching dot");
			e.printStackTrace();
			System.exit(1);
		}

		File dotFile = new File("graph-" + methodNumber + ".dot");
		dotFile.delete();

		System.out.println("System pass");
	}

}
