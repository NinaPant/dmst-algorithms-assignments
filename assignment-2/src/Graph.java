import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {
	
	public static class Node implements Comparable<Node> {
		public static final Node Empty = new Node(-1);
	    public List<Edge> adjacencies;
	    public int distance = Integer.MAX_VALUE;
	    public Node previous = Empty;
	    public int index;
	    public Node(int index) {
	    	this.index = index;
	    	this.adjacencies = new ArrayList<Edge>();
	    }
	    
	    public int compareTo(Node compared) {
	    	return Integer.compare(distance, compared.distance);
	    }
	}
	
	public static class Edge {
		public int weight;
		public Node target;
		public Edge(Node targetNode, int edgeWeight) {
			this.target = targetNode;
			this. weight = edgeWeight;
		}
	}
	
	public static int nodesSize = 0;
	
	public static Node[] nodes;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int argPart = 0;
		int startingNodeIndex;
		
		boolean directed = true;
		if (args[0].equals("-u")) {
			directed = false;
			argPart = 1;
		}
		
		if (args[argPart].equals("-s")) {
			startingNodeIndex = Integer.parseInt(args[argPart+1]);
			readGraphFile(args[args.length-1],directed);
			computePaths(nodes[startingNodeIndex]);
			
			String predecessors = "[";
			String distances = "[";
			
			for (int i = 0; i<nodesSize; i++ ) {
				predecessors = predecessors + nodes[i].previous.index;
				distances = distances + nodes[i].distance;
				if (i < nodesSize - 1) {
					predecessors = predecessors+ ", ";
					distances = distances+ ", ";
				}
			}
			predecessors = predecessors + "]";
			distances = distances + "]";
			
			System.out.println("Predecessor matrix");
			System.out.println(predecessors);
			System.out.println("Distance matrix");
			System.out.println(distances);
		}
		
		if (args[argPart].equals("-a")) {
			Node nodeToCompute;
			
			readGraphFile(args[args.length-1],directed);
			
			String predecessorsResults[] = new String[nodesSize];
			String distancesResults[] = new String[nodesSize];
			
			for (int nc = 0; nc<nodesSize; nc++) {
				nodeToCompute = nodes[nc];
				computePaths(nodeToCompute);
				
				String predecessors = "[";
				String distances = "[";
				
				for (int i = 0; i<nodesSize; i++ ) {
					predecessors = predecessors + nodes[i].previous.index;
					distances = distances + nodes[i].distance;
					if (i < nodesSize - 1) {
						predecessors = predecessors+ ", ";
						distances = distances+ ", ";
					}
				}
				predecessors = predecessors + "]";
				distances = distances + "]";
				
				predecessorsResults[nc] = predecessors;
				distancesResults[nc] = distances;
			}
			
			System.out.println("Predecessor matrix");
			for (int i = 0; i <nodesSize; i++) {
				System.out.println(predecessorsResults[i]);
			}
			
			System.out.println("Distance matrix");
			for (int i = 0; i <nodesSize; i++) {
				System.out.println(distancesResults[i]);
			}
		}
		
		if (args[argPart].equals("-d")) {
			Node nodeToCompute;
			int maximumMinDistance = 0;
			
			readGraphFile(args[args.length-1],directed);
			
			for (int nc = 0; nc<nodesSize; nc++) {
				nodeToCompute = nodes[nc];
				
				computePaths(nodeToCompute);
				
				for ( int i = 0; i< nodesSize; i++ ) {
					if (nodes[i].distance > maximumMinDistance ) {
						maximumMinDistance = nodes[i].distance;
					}
				}
			}
			System.out.println(maximumMinDistance);
		}
	}
	public static void readGraphFile(String filename, boolean directed) {
		List<String> lines;
		int linesSize = 0;
		
		String[] lineparts;
		
		Node targetNode;
		Node currentNode;
		int weight;
		
		boolean weighted = false;
		
		try {
			
			lines = Files.readAllLines(Paths.get(filename),Charset.defaultCharset());
			linesSize = lines.size();
			
			//determine node size
			for (int i = 0; i < linesSize; i++) {
				lineparts = lines.get(i).split(" ");
				if (Integer.parseInt(lineparts[0]) > nodesSize-1) {
					nodesSize = Integer.parseInt(lineparts[0]) + 1;
				}
				if (Integer.parseInt(lineparts[1]) > nodesSize-1) {
					nodesSize = Integer.parseInt(lineparts[1]) + 1;
				}
			}
			
			//is weighted?
			lineparts = lines.get(0).split(" ");
			if (lineparts.length == 3 ) {
				weighted = true;
			}
			
			//build graph
			
			nodes = new Node[nodesSize];

			for (int i = 0; i < nodesSize; i++ ) {
				nodes[i] = new Node(i);
			}
			
			for (int i = 0; i < linesSize; i++) {
				lineparts = lines.get(i).split(" ");

				currentNode = nodes[Integer.parseInt(lineparts[0])];
				targetNode = nodes[Integer.parseInt(lineparts[1])];
				weight = 1;
				if (weighted) {
					weight = Integer.parseInt(lineparts[2]);
				}
				currentNode.adjacencies.add(new Edge(targetNode,weight));
				
				if (!directed) {
					targetNode.adjacencies.add(new Edge(currentNode,weight));
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	public static void computePaths(Node startingNode) {
		//zeroing of all nodes
		for ( Node node: nodes ) {
			node.distance = Integer.MAX_VALUE;
			node.previous = Node.Empty;
		}
		
		startingNode.distance = 0;
		PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
		Node currentNode, targetNode;
		
		nodeQueue.add(startingNode);
		
		while ( !nodeQueue.isEmpty()){
			currentNode = nodeQueue.poll();
			
			for (Edge currentEdge : currentNode.adjacencies) {
				targetNode = currentEdge.target;
				if (currentNode.distance + currentEdge.weight < targetNode.distance ) {
					nodeQueue.remove(targetNode);
					
					targetNode.distance = currentNode.distance + currentEdge.weight;
					
					targetNode.previous = currentNode;
					nodeQueue.add(targetNode);
				}
			}
		}
	}
}
