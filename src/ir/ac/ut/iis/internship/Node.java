package ir.ac.ut.iis.internship;

public class Node {
	int id;
	String url;
	
	public Node(int i) {
		id = i;
		url = new String("abc" + i);
	}
}