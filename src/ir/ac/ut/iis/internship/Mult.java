package ir.ac.ut.iis.internship;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;


public class Mult {
	public static double[] matrixMult(Graph graph, Table<Integer, Integer, Double> a, double[] t_star, int n) {

		double[] result = new double[n];
		Set<Cell<Integer, Integer, Double>> cs = a.cellSet();
		Iterator<Cell<Integer, Integer, Double>> it1 = cs.iterator();
		while (it1.hasNext()) {
			Cell<Integer, Integer, Double> currentCell = (Cell<Integer, Integer, Double>) it1.next();
			int p = currentCell.getRowKey();
			Map<Integer, Double> map = a.row(p);
			Set<Integer> ks = map.keySet();
			Iterator<Integer> it2 = ks.iterator();
			double partialSum = 0;
			while (it2.hasNext()) {
				Integer currentInteger = it2.next();
				partialSum += map.get(currentInteger) * t_star[currentInteger];
			}
			result[p] = partialSum;
		}

		return result;
	}

	public static double[] scalarMult(double[] d, double scalar, int n) {
		double[] result = new double[n];
		for (int i = 0; i < n; i++)
			result[i] =  (d[i] * scalar);
		return result;
	}
}
