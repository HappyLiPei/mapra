package network;

import java.util.HashMap;
import java.util.HashSet;

import io.FileUtilities;

public class CytoscapeFileGenerator {
	
	//standard header of xgmml file
	private static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+"\n"+
			"<graph label=\"disease network\"" +"\n"+ 
			"xmlns:dc=\"http://purl.org/dc/elements/1.1/\""+"\n"+
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\""+"\n"+
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"+
			"xmlns:cy=\"http://www.cytoscape.org\""+"\n"+
			"xmlns=\"http://www.cs.rpi.edu/XGMML\">"+"\n";
	//standard end of xgmml file
	private static final String end = "</graph>";
	
	//color set used for nodes of the network
	private static final String PINK="#F53FD4";		// color for selected nodes
	private static final String YELLOW="#F5BB3F";	// color for highlighted nodes
	private static final String BLUE="#3F79F5";		// standard color of all remaining nodes
	
	/**
	 * method to write an xgmml file representing a network of diseases
	 * @param output: path to xgmml file that is created by this method
	 * @param d: symmetric distance or similarity matrix, which is the basis of the network
	 * @param cutoff: score threshold for defining an edge between two diseases
	 * @param smallerthan: defines rule for setting up edges
	 * 						smallerthan = true -> d is interpreted as distance matrix
	 * 												edge(i,j) if d[i][j]<threshold
	 * 						smallerthan = false -> d is interpreted as similarity matrix
	 * 												edge(i,j) if d[i][j]>threshold
	 * @param selection: set containing all ids whose nodes should be colored (highlighted) in the network
	 * @param res:	scores from Phenomizer corresponding to the ids in selection
	 * @param names: names of all diseases stored in d
	 */
	public static void writeSelectedXGMML(String output, DistanceMatrix d, double cutoff, boolean smallerthan,
			HashSet<String> selection, PhenoResults res, HashMap<Integer, String> names){
		
		FileUtilities.writeString(output, header);
		
		//iterate over all nodes = diseases
		for(int i=0; i<d.size();i++){
			//determine if node has any edges,
			//singleton=true <-> node does not have edges, singleton =false <-> node has at least one edge
			boolean singleton = true;
			for(int j=0; j<d.size(); j++){
				if(j==i){
					continue;
				}
				//distance matrix
				if(smallerthan){
					if(d.get(i, j)<cutoff){
						singleton = false;
					}
				}
				//similarity matrix
				else{
					if(d.get(i, j)>cutoff){
						singleton = false;
					}
				}
			}
			// create xgmml representation of nodes, if node has at least one edge or if it is part of the Phenomizer resulrs
			if(selection.contains(d.IdAt(i))||!singleton){
				//disease name is available -> name as node label
				if(names.containsKey(Integer.valueOf(d.IdAt(i)))){
					FileUtilities.writeStringToExistingFile(output,
							NodeTag(d.IdAt(i), names.get(Integer.valueOf(d.IdAt(i))),selection.contains(d.IdAt(i)),res.getResFor(d.IdAt(i))));
				}
				//disease name not available -> id as node label
				else{
					FileUtilities.writeStringToExistingFile(output,
							NodeTag(d.IdAt(i), d.IdAt(i),selection.contains(d.IdAt(i)),res.getResFor(d.IdAt(i))));
				}
			}	
		}
		
		//iterate over all pairs of disease = nodes to find edges
		for(int i=0; i<d.size(); i++){
			for(int j=i+1; j<d.size(); j++){
				//edge rule for distance matrix
				if(smallerthan){
					if(d.get(i, j)<cutoff){
						FileUtilities.writeStringToExistingFile(output, EdgeTag(d.IdAt(i), d.IdAt(j)));
					}
				}
				//edge rule for similarity matric
				else{
					if(d.get(i, j)>cutoff){
						FileUtilities.writeStringToExistingFile(output, EdgeTag(d.IdAt(i), d.IdAt(j)));
					}
				}
			}
		}
		
		FileUtilities.writeStringToExistingFile(output, end);
	}
	
	/**
	 * generates node tag in xgmml format representing a disease node of the network
	 * @param nodeid: PhenoDis disease id
	 * @param nodename: name of the node, either PhenoDis disease id or PhenoDis disease name
	 * @param selected: selected = true -> node is part of Phenomizer result and gets colored in yellow
	 * @param scores: scores != null -> node is part of Phenomizer results
	 * 					scores[0]: similarity score of Phenomizer
	 * 					scores[1]: p value of Phenomizer (optional)
	 * @return: node tag for xgmml file
	 */
	private static String NodeTag (String nodeid, String nodename, boolean selected, String[] scores){
		
		String scoreinfo="";
		String nodecolor="";
		if(selected){
			nodecolor=YELLOW;
			if(scores!=null){
				scoreinfo+="<att name=\"score\" value=\""+scores[0]+"\" type=\"real\"/>"+"\n";
				if(scores.length==2){
					scoreinfo+="<att name=\"pvalue\" value=\""+scores[1]+"\" type=\"real\"/>"+"\n";
				}
			}
		}
		else{
			nodecolor=BLUE;
		}

		return "<node label=\""+nodename+"\" id=\""+nodeid+"\">" + "\n" +
				"<att name=\"selected\" value=\"0\" type=\"boolean\"/>"+ "\n" +
				"<graphics width=\"1.0\" w=\"70.0\" fill=\""+nodecolor+"\" type=\"ELLIPSE\" z=\"0.0\" outline=\"#323232\">"+"\n"+
				"<att name=\"NODE_LABEL_COLOR\" value=\"#323232\" type=\"string\"/>"+"\n" +
				"<att name=\"NODE_SELECTED_PAINT\" value=\""+PINK+"\" type=\"string\"/>"+"\n" +
				"</graphics>"+"\n"+
				scoreinfo+
				"</node>"+"\n";
	}
	
	/**
	 * generates edge tag in xgmml format representing an edge between two disease nodes of the network
	 * @param id1: PhenoDis id of the first node
	 * @param id2: PhenoDis id of the second node
	 * @return: edge tag for xgmml file
	 */
	private static String EdgeTag (String id1, String id2){
		return "<edge label=\"" + id1+ "-" + id2 + "\" source=\"" + id1 + "\" target=\"" + id2 +"\">" + "\n" +
				"<graphics width=\"0.75\" fill=\"#323232\">\"" + "\n" + 
				"</graphics>" +"\n" +
				"</edge>" + "\n";
	}
	
	/**
	 * method to write a script in Cytoscape language for automatically loading the disease network into Cytoscape
	 * @param networkfile: location of the xgmml file representing the network
	 * @param scriptfile: file which is written by this method
	 */
	public static void WriteScript(String networkfile, String scriptfile){
		FileUtilities.writeString(scriptfile, "network load file file=\""+networkfile+"\"\n"+
				"view create"+"\n"+
				"layout force-directed");			
	}

}
