package network;

import java.util.HashMap;
import java.util.HashSet;

import main.FileUtilities;

public class CytoscapeFileGenerator {
	
	
	private static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+"\n"+
			"<graph label=\"disease network\"" +"\n"+ 
			"xmlns:dc=\"http://purl.org/dc/elements/1.1/\""+"\n"+
			"xmlns:xlink=\"http://www.w3.org/1999/xlink\""+"\n"+
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+"\n"+
			"xmlns:cy=\"http://www.cytoscape.org\""+"\n"+
			"xmlns=\"http://www.cs.rpi.edu/XGMML\">"+"\n";
	
	private static final String end = "</graph>";
	
	private static final String PINK="#F53FD4";
	private static final String YELLOW="#F5BB3F";
	private static final String BLUE="#3F79F5";

	public static void writeXGMML(String output, DistanceMatrix d, double cutoff){
		FileUtilities.writeString(output, header);
		for(int i=0; i<d.size();i++){
			FileUtilities.writeStringToExistingFile(output,(NodeTag(d.IdAt(i),d.IdAt(i), false,null)));
		}
		for(int i=0; i<d.size(); i++){
			for(int j=i+1; j<d.size(); j++){
				if(d.get(i, j)<cutoff){
					FileUtilities.writeStringToExistingFile(output,EdgeTag(d.IdAt(i), d.IdAt(j)));
				}
			}
		}
		FileUtilities.writeString(output, end);
	}

	public static void writeSelectedXGMML(String output, DistanceMatrix d, double cutoff, boolean smallerthan,
			HashSet<String> selection, PhenoResults res, HashMap<Integer, String> names){
		
		FileUtilities.writeString(output, header);
		for(int i=0; i<d.size();i++){
			//ignore singletons
			boolean singleton = true;
			for(int j=0; j<d.size(); j++){
				if(j==i){
					continue;
				}
				if(smallerthan){
					if(d.get(i, j)<cutoff){
						singleton = false;
					}
				}
				else{
					if(d.get(i, j)>cutoff){
						singleton = false;
					}
				}
			}
			if(selection.contains(d.IdAt(i))||!singleton){
				if(names.containsKey(Integer.valueOf(d.IdAt(i)))){
					FileUtilities.writeStringToExistingFile(output,
							NodeTag(d.IdAt(i), names.get(Integer.valueOf(d.IdAt(i))),selection.contains(d.IdAt(i)),res.getResFor(d.IdAt(i))));
				}
				else{
					FileUtilities.writeStringToExistingFile(output,
							NodeTag(d.IdAt(i), d.IdAt(i),selection.contains(d.IdAt(i)),res.getResFor(d.IdAt(i))));
				}
			}	
		}
		for(int i=0; i<d.size(); i++){
			for(int j=i+1; j<d.size(); j++){
				if(smallerthan){
					if(d.get(i, j)<cutoff){
						FileUtilities.writeStringToExistingFile(output, EdgeTag(d.IdAt(i), d.IdAt(j)));
					}
				}
				else{
					if(d.get(i, j)>cutoff){
						FileUtilities.writeStringToExistingFile(output, EdgeTag(d.IdAt(i), d.IdAt(j)));
					}
				}
			}
		}
		FileUtilities.writeStringToExistingFile(output, end);

	}

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

	private static String EdgeTag (String id1, String id2){
		return "<edge label=\"" + id1+ "-" + id2 + "\" source=\"" + id1 + "\" target=\"" + id2 +"\">" + "\n" +
				"<graphics width=\"0.75\" fill=\"#323232\">\"" + "\n" + 
				"</graphics>" +"\n" +
				"</edge>" + "\n";
	}
	
	public static void WriteScript(String networkfile, String scriptfile){
		FileUtilities.writeString(scriptfile, "network load file file=\""+networkfile+"\"\n"+
				"view create"+"\n"+
				"layout force-directed");			
	}

}
