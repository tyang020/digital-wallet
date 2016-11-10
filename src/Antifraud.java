// example of program that detects suspicious transactions
// fraud detection algorithm
import java.io.*;
import java.util.*;

class Antifraud {
	final static String SPLITTER = ",";
	final static String TRUSTED = "trusted";
	final static String UNVERIFIED = "unverified";
	
    public static void main(String[] args) {
    	if (args.length != 5) {
    		System.out.print("Unvalid parameters!");
    		return;
    	}
    	
    	final String BATCH_FILE = args[0];
    	final String STREAM_FILE = args[1];
    	final String OUTPUT_FILE1 = args[2];
    	final String OUTPUT_FILE2 = args[3];
    	final String OUTPUT_FILE3 = args[4];
    	
        HashMap<String, HashSet<String>> graph = initialGraph(BATCH_FILE);
        
        BufferedReader br = null;
        BufferedWriter bw1 = null;
        BufferedWriter bw2 = null;
        BufferedWriter bw3 = null;
        
        String line = "";
        System.out.println("Processing record...");
        long startTime = System.currentTimeMillis();
        try {
        	br = new BufferedReader(new FileReader(STREAM_FILE));
        	bw1 = new BufferedWriter(new FileWriter(OUTPUT_FILE1));
        	bw2 = new BufferedWriter(new FileWriter(OUTPUT_FILE2));
        	bw3 = new BufferedWriter(new FileWriter(OUTPUT_FILE3));
            br.readLine();
            while ((line = br.readLine()) != null) {
            	if (!Character.isDigit(line.charAt(0))) continue;
            	String[] tran = line.split(SPLITTER);
            	String user1 = tran[1];
            	String user2 = tran[2];
            	String f1_output = UNVERIFIED;
            	String f2_output = UNVERIFIED;
            	String f3_output = UNVERIFIED;
            	// Check if two users are new user who have never paid anyone before.
            	if (!graph.containsKey(user1) || !graph.containsKey(user2)) {
        			if (!graph.containsKey(user1)) graph.put(user1, new HashSet<String>());
        			if (!graph.containsKey(user2)) graph.put(user2, new HashSet<String>());
        		}
            	// Check if we should send warning in each feature by using bfs.
            	else {
            		// Feature 1: check if two users are neighbors
            		if (graph.get(user1).contains(user2)) {
            			f1_output = TRUSTED;
            			f2_output = TRUSTED;
            			f3_output = TRUSTED;
            		} else {
            			// Feature 2: Check if two users have a common neighbor
            			for (String s : graph.get(user1)) {
            				if (graph.get(user2).contains(s)) {
            					f2_output = TRUSTED;
            					f3_output = TRUSTED;
            					break;
            				}
            			}
            			
            			// Feature 3: Check if two users are within 4-degree network using bfs
            			if (f3_output.equals(UNVERIFIED) && isFriend(graph, user1, user2, 4))
            				f3_output = TRUSTED;
            		}
            	}
            	bw1.write(f1_output);
            	bw1.newLine();
            	bw2.write(f2_output);
            	bw2.newLine();
            	bw3.write(f3_output);
            	bw3.newLine();
            	
            	graph.get(user1).add(user2);
            	graph.get(user2).add(user1);
            }
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
                bw1.close();
                bw2.close();
                bw3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.print("Finished processing all records...");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime/1000.0 + "s");
    }
    
    
    /**
     * This function is to initialize the graph from batch_payment.csv. 
     * @param file
     * @return
     */
       
    public static HashMap<String, HashSet<String>> initialGraph(String file) {
        BufferedReader br = null;
        String line = "";
        HashMap<String, HashSet<String>> graph = null;
        System.out.println("Reading batch_payment...");
        long startTime = System.currentTimeMillis();
        try {
            br = new BufferedReader(new FileReader(file));
            br.readLine();
            
            // Use a map to represent a graph:
            // Key is a node, and value is a set of neighbors connected to that node.
            graph = new HashMap<>();
            while ((line = br.readLine()) != null) {
            	if (!Character.isDigit(line.charAt(0))) continue;
                String[] tran = line.split(SPLITTER);
                
                // Check if graph already contains the nodes
                if (!graph.containsKey(tran[1]))
                    graph.put(tran[1], new HashSet<String>());
                if (!graph.containsKey(tran[2]))
                    graph.put(tran[2], new HashSet<String>());
                
                // Add the nodes to graph.
                graph.get(tran[1]).add(tran[2]);
                graph.get(tran[2]).add(tran[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.print("Finished reading batch_payment...");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime/1000.0 + "s");
        return graph;
    }
    
    /** 
     * This function checks if two users have paid each other before.
     * @param graph
     * @param user1
     * @param user2
     * @param degree
     * @return
     */
    public static boolean isFriend(HashMap<String, HashSet<String>> graph, String user1, String user2, int degree) {
    	HashSet<String> beginSet = new HashSet<>(), endSet = new HashSet<>(), visited = new HashSet<String>();
    	beginSet.add(user1);
    	endSet.add(user2);
    	visited.add(user1);
    	visited.add(user2);
    	
    	int level = 0;
    	while (level < degree && !beginSet.isEmpty() && !endSet.isEmpty()) {
    		if (beginSet.size() > endSet.size()) {
    			HashSet<String> temp = beginSet;
    			beginSet = endSet;
    			endSet = temp;
    		}
    		
    		HashSet<String> nextLevelSet = new HashSet<String>();
    		for (String user : beginSet) {
    			for (String childUser : graph.get(user)) {
    				if (endSet.contains(childUser))
    					return true;
    				
    				if (!visited.contains(childUser)) {
    					visited.add(childUser);
    					nextLevelSet.add(childUser);
    				}
    			}
    		}
    		beginSet = nextLevelSet;
    		level++;
    	}
    	return false;
    }
}