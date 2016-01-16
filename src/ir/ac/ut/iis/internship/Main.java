package ir.ac.ut.iis.internship;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		File file = new File("uk200705hostgraphweighted.txt");
		FileReader reader = new FileReader(file);
		BufferedReader in = new BufferedReader(reader);
		Scanner cin = new Scanner(in);

		int n = cin.nextInt();
		Graph g = new Graph(n);

		System.out.println(cin.nextLine());


		for (int i = 0; i < n; i++) {

			int src = i;
			if (! g.map.containsKey(src))
				g.map.put(new Integer(src), new Node(src));
			ArrayList<Node> nal = new ArrayList<Node>();

			boolean isBlank = false;
			String currentLine = new String(cin.nextLine());

			if (currentLine.equals("")) {
				System.out.println("blank line at " + i);
				isBlank = true;
				// nothing to do here
				g.edges.put(g.map.get(src), nal);
				continue;

			}
			else { 

				String khat = new String(currentLine);
				String [] words = khat.split("\\s+");

				for (int j = 0; j < words.length; j++) {
					String[] tajzie = words[j].split(":");
					int dest = Integer.parseInt(tajzie[0]);
					int weight = Integer.parseInt(tajzie[1]);

					if (g.map.containsKey(dest)) {
						nal.add(g.map.get(dest));
						g.outlinks[src] = g.outlinks[src] + weight;
						g.inlinks[dest] = g.inlinks[dest] + weight;
					}
					else {
						g.map.put(new Integer(dest), new Node(dest));
						nal.add(g.map.get(dest));
						g.outlinks[src] = g.outlinks[src] + weight;
						g.inlinks[dest] = g.inlinks[dest] + weight;
					}
				}
			}

			g.edges.put(g.map.get(src), nal);

		}
		cin.close();


		File f = new File("alllabels.txt");
		FileReader r = new FileReader(f);
		BufferedReader br = new BufferedReader(r);
		Scanner sc = new Scanner(br);


		while (sc.hasNextLine()){
			String currentLine = new String(sc.nextLine());
			String [] words = currentLine.split("\\s+");
			g.labels.put(Integer.valueOf(words[0]), words[1]);
		}

		sc.close();

		g.findTranMap();
		g.findInvTranMap();
		g.trustRank(2000, 0.85, 5);
		g.printMatToFile(g.trustRankScores, "results.txt");

	}

}
