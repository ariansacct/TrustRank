package ir.ac.ut.iis.internship;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;


public class Graph {
	int noOfVertices;
	Map<Node, ArrayList<Node>> edges;
	Map<Integer, Node> map;
	Table<Integer, Integer, Double> tranMap;
	Table<Integer, Integer, Double> invTranMap;
	double[] inlinks;
	double[] outlinks;
	double[] trustRankScores;
	double[] selectSeedScores;
	Map<Integer, String> labels;

	public Graph(int n) {
		noOfVertices = n;
		edges = new HashMap<Node, ArrayList<Node>>(n);
		map = new HashMap<Integer, Node>(n);
		tranMap = HashBasedTable.create();
		invTranMap = HashBasedTable.create();
		inlinks = new double[n];
		outlinks = new double[n];
		labels = new HashMap<Integer, String>();

		selectSeedScores = new double[2];

	}

	public void printMat(double[] mat) {
		for (int i = 0; i < noOfVertices; i++) {
			System.out.print(mat[i]);
			if (i != noOfVertices - 1)
				System.out.print('\t');
		}
	}

	public void printMatToFile(double[] trustRankScores, String fileName) throws FileNotFoundException, UnsupportedEncodingException {

		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		for (int i = 0; i < noOfVertices; i++) {

			writer.print(i);
			writer.print('\t');
			writer.print(String.valueOf(trustRankScores[i]));

			if (i != noOfVertices - 1)
				writer.print('\n');
		}

		writer.close();
	}

	public void printTranMap() {
		Set<Cell<Integer, Integer, Double>> s = tranMap.cellSet();
		Iterator<Cell<Integer, Integer, Double>> it = s.iterator();
		System.out.println("non-zeros:");
		while (it.hasNext()) {
			Cell<Integer, Integer, Double> current =
					(Cell<Integer, Integer, Double>) it.next();
			System.out.println("row = " + current.getRowKey() + ", col = " +
					current.getColumnKey() + ", value = " + current.getValue());
		}
		System.out.println("The rest are zeros.");
	}

	public void printInvTranMap() {
		Set<Cell<Integer, Integer, Double>> s = invTranMap.cellSet();
		Iterator<Cell<Integer, Integer, Double>> it = s.iterator();
		System.out.println("non-zeros:");
		while (it.hasNext()) {
			Cell<Integer, Integer, Double> current =
					(Cell<Integer, Integer, Double>) it.next();
			System.out.println("row = " + current.getRowKey() + ", col = " +
					current.getColumnKey() + ", value = " + current.getValue());
		}
		System.out.println("The rest are zeros.");
	}

	public void findTranMap() {

		for (int q = 0; q < noOfVertices; q++) {
			ArrayList<Node> al = edges.get(map.get(q));
				for (int i = 0; i < al.size(); i++) {
					int p = al.get(i).id; // (q,p) is an edge
					tranMap.put(new Integer(p), new Integer(q), new Double(1 / outlinks[q]));
				}
		}
	}

	public void findInvTranMap() {


		for (int p = 0; p < noOfVertices; p++) {
			ArrayList<Node> al = edges.get(map.get(p));
				for (int i = 0; i < al.size(); i++) {
					int q = al.get(i).id; // (p,q) is an edge
					invTranMap.put(new Integer(p), new Integer(q), new Double(1 / inlinks[q]));
				}
		}
	}

	public double[] selectSeed(double decay, int iters) {
		double[] s = new double[noOfVertices];
		Arrays.fill(s, 1);
		double[] mat1;
		for (int i = 0; i < iters; i++) {

			mat1 = Mult.scalarMult(Mult.matrixMult(this, invTranMap, s, noOfVertices), decay, noOfVertices);
			for (int j = 0; j < noOfVertices; j++)
				s[j] =  (mat1[j] + (1 - decay) * 1 / noOfVertices);
		}

		this.selectSeedScores = s;

		return s;
	}

	public void trustRank(int oInvocations,
			double decay, int iters) throws IOException {
		
		double df = decay;
		int iterations = iters;
		
		double[] d = new double[noOfVertices];

		File f = new File("alllabels.txt");
		FileReader r = new FileReader(f);
		BufferedReader br = new BufferedReader(r);
		Scanner sc = new Scanner(br);

		int counter=0;
		while (sc.hasNextLine() && counter!=2000) {
			String line = sc.nextLine();
			String [] words = line.split("\\s+");
			int nodeId = Integer.parseInt(words[0]);
			String label = words[1];

			if (label.equals("nonspam")) {
				d[nodeId] = 1;
				counter++;
			}

		}

		sc.close();

		double noOfOnes = 0;
		for (int j = 0; j < noOfVertices; j++)
			if (d[j] == 1)
				noOfOnes++;
		d = Mult.scalarMult(d, 1/noOfOnes, noOfVertices);

		double[] t_star = d.clone();

		// troubleshooting
		PrintWriter pw = new PrintWriter("errors.txt", "UTF-8");
		
		for (int j = 0; j < iterations; j++) {
			t_star = Mult.scalarMult(Mult.matrixMult(this, tranMap, t_star, noOfVertices), df, noOfVertices);
			for (int k = 0; k < noOfVertices; k++) {
				if (t_star[k] == Double.NaN) {
					pw.println("NaN at line "+ k);
				}
				t_star[k] =  (t_star[k] + d[k] * (1 - df));
			}
		}
		
		pw.close();

		trustRankScores = t_star;

	}
}
