package clustering;

import main.FileUtilities;

public class RscriptClustGenerator {
	
	private static final String RCODE=
			"args<-commandArgs(TRUE)" +"\n" +
			"ids<-args[(3:length(args))]"+"\n" +
			"pheno<-as.integer(ids)"+"\n" +
			"tmpMatrix <- read.table(args[1],sep=\",\")"+"\n"+
			"tmpLabels <- tmpMatrix[2:length(tmpMatrix[2,]),1]"+"\n" +
			"tmpMatrix <- tmpMatrix[2:length(tmpMatrix[2,]),2:length(tmpMatrix[,2])]"+"\n" +
			"distMatrix <- as.dist(tmpMatrix)"+"\n" +
			"clustering <- hclust(distMatrix,method=\"complete\")"+"\n" +
			"clustering$labels <- tmpLabels"+"\n" +
			"phenoTmp <- cbind(clustering$order,-1)"+"\n"+
			"for(j in 1:length(phenoTmp[,1])){" + "\n" +
			"\t"+"index <- phenoTmp[j,1]" +"\n" +
			"\t"+"label <- clustering$labels[index]"+"\n" +
			"\t"+"if(label %in% pheno){"+"\n" +
			"\t\t"+"phenoTmp[j,2]<-0"+"\n" +
	  		"\t"+"}"+"\n" +
			"}"+"\n" +
			"jpeg(args[2])"+"\n" +
			"plot(clustering,hang=-1,ylim=c(0,1.2),ylab=\"\",yaxt=\"n\",ann=FALSE)"+"\n"+
			"par(new=TRUE)"+"\n" +
			"plot(phenoTmp[,2],xlab=\"\",ylab=\"\",ylim=c(-0.2,1.2), col=\"red\",xaxt=\"n\",yaxt=\"n\",ann=FALSE)"+"\n" +
			"dev.off()";
	
	public static void writeClusteringScript(String scriptfile){
		FileUtilities.writeString(scriptfile, RCODE);		
	}

}
