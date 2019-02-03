package br.edu.ufrj.lwcoedge.experiment.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilCSV;

@Service
public class CSVFile {

	public void graph(String csvFile,
			String title, String labelX, String labelY, 
			ArrayList<String> variation, ArrayList<String> subtitles, 
			Map<String, ArrayList<Float>> subtitleValues,
			Map<String, ArrayList<Float>> confidenceInterval) {
		
        FileWriter writer = null;
		try {
			writer = new FileWriter(csvFile);
	        UtilCSV.writeLine(writer, title);
	        UtilCSV.writeLine(writer, labelX);
	        UtilCSV.writeLine(writer, labelY);
	        UtilCSV.writeLine(writer, variation);

	        for (String subtitle : subtitles) {
	        	ArrayList<String> values = new ArrayList<String>();
	        	ArrayList<Float> sbtValues = subtitleValues.get(subtitle);
	        	for (Float v : sbtValues) {
	        		values.add( String.valueOf(v) );
	        	}
	            UtilCSV.writeLine(writer, values);
			}
	        for (String subtitle : subtitles) {
	        	ArrayList<String> values = new ArrayList<String>();
	        	for (Float v : confidenceInterval.get(subtitle)) {
	        		values.add( String.valueOf(v) );
	        	}
	            UtilCSV.writeLine(writer, values);
			}
	        for (String subtitle : subtitles) {
	            UtilCSV.writeLine(writer, subtitle);
			}
	        writer.flush();
	        writer.close();

		} catch (IOException e) {
			System.out.println(Util.msg("The file ", csvFile, " did not generate!\n",e.getMessage()));
		}
                
	}
}
