import java.util.*;
import java.io.*;

/*
 * Kevin Shi                     TA: Effie Zheng
 * 
 * The HuffmanCode class handles the creation and interpretation of cryptic
 * cyphers created by using the Huffman encoding scheme
 * It also perform operations to read or write to file the cyphers
 */

public class HuffmanCode {
	
	// node pointing to the current tree structure
	private HuffmanNode overallRoot;
    
	// pre: takes in an integer array of frequencies
	//      the index represents the ascii number
	//      the value represents the frequency of that specific character
	// post: constructs the huffman tree with lower frequency characters
	//       on the bottom, and higher frequency charcters towards the top
	//       original ordering is preserved with equal frequencies
	public HuffmanCode(int[] frequencies) {
		// priority queue to remove nodes with lowest frequency
		Queue<HuffmanNode> prioQueue = new PriorityQueue<>();
		// build up priority queue
		for(int i = 0; i < frequencies.length; i++) {
			int frequency = frequencies[i];
			// don't add characters that never even appeared
			if(frequency > 0) {
				prioQueue.add(new HuffmanNode(i, frequency));
			}
		}
		// build up the huffman tree
		while(prioQueue.size() > 1) {
			prioQueue.add(new HuffmanNode(prioQueue.remove(), prioQueue.remove()));
		}
		// assign the tree to overallRoot
		overallRoot = prioQueue.remove();
	}
	
	// pre: takes in a scanner object input
	// post: rebuild the huffman tree for de-ciphering
	public HuffmanCode(Scanner input) {
		// build the tree one node at a time
		// use the x = change(x) strucuture to add to the binary tree
		while(input.hasNextLine()) {
			// always handle line by line, not by token characters
			int data = Integer.parseInt(input.nextLine());
			String path = input.nextLine();
			overallRoot = readHelper(data, path, overallRoot);
		}
	}
	
	// pre: takes an int data, String path, and a HuffmanNode
	// post: builds up all the HuffmanNode(s) required to reach the described path 
	//       and stores the character value data
	//       returns a HuffmanNode
	private HuffmanNode readHelper(int data, String path, HuffmanNode root) {
		// all frequency are set to 0, this value won't be accessed or used in anyway
		if(path.isEmpty()) {
			return new HuffmanNode(data, 0);
		}
		// if no node currently exist, fill it with a dummy node
		if(root == null) {
			root = new HuffmanNode(0,0);
		}
		// recurse depending on the path number
		if(path.startsWith("0")) {
			root.zeroLeft = readHelper(data, path.substring(1), root.zeroLeft);
		}else {
			root.oneRight = readHelper(data, path.substring(1), root.oneRight);
		}
		return root;
	}
	
	// pre: takes a PrintStream object output
	// post: saves the current Huffman tree to a text file
	public void save(PrintStream output) {
		// kick start recursion with default values
		save(output, overallRoot, "");
	}
	
	// pre: takes a PrintStream object, a HuffmanNode, 
	//      and a String soFar representing the current path
	// post: forms a public/private pair with save method
	//       writes all the leaves of the tree and their path in a 
	//       pre-order traversal
	private void save(PrintStream output, HuffmanNode root, String soFar) {
		if(root != null) {
			if(root.oneRight == null && root.zeroLeft == null) {
				output.println(root.data);
				output.println(soFar);
			} else {
				save(output, root.zeroLeft, soFar + "0");
				save(output, root.oneRight, soFar + "1");
			}
		}
	}
	
	// pre: takes in a BitInputStream and a PrintStream object
	// post: the cipher text is decoded and passed to PrintStream object
	public void translate(BitInputStream input, PrintStream output) {
		HuffmanNode current = overallRoot;
		while(input.hasNextBit()) {
			if(current.oneRight == null && current.zeroLeft == null) {
				output.write(current.data);
				// reset current to top of tree
				current = overallRoot;
			}else {
				if(input.nextBit() == 0) {
					current = current.zeroLeft;
				}else {
					current = current.oneRight;
				}
			}
		}
		
		// input has no bits left but since the encoded message is correct and to style
		// current is now on a leave node representing the last encoded character
		// also note that all branch nodes have value 0, null char
		output.write(current.data);
	}
	
	
	/*
	 * private class that represents a single HuffmanNode
	 * used to build the huffman binary tree	
	 */
	private static class HuffmanNode implements Comparable<HuffmanNode>{
		public int data;
		public int frequency;
		public HuffmanNode zeroLeft;
		public HuffmanNode oneRight;
		
		// pre: takes in int data and frequency
		// post: constructs a HuffmanNode with  data, and its frequency
		public HuffmanNode(int data, int frequency) {
			this.data = data;
			this.frequency = frequency;
			zeroLeft = null;
			oneRight = null;
		}
		
		// pre: takes two HuffmanNode
		// post: constructs a new HuffmanNode using two other HuffmanNodes
		//       used when combining nodes in priority queue
		public HuffmanNode(HuffmanNode node1, HuffmanNode node2) {
			// empty char placeholder, has no value
			this.data = 0;
			this.frequency = node1.frequency + node2.frequency;
			zeroLeft = node1;
			oneRight = node2;
		}
		
		// pre: takes an HuffmanNode
		// post: return positive if current frequency is higher
		//       return negative if current frequency is lower
		//       return 0 if frequencies are equal
		public int compareTo(HuffmanNode other) {
			return this.frequency - other.frequency;
		}
	}
}
