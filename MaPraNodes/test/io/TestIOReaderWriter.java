package io;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import io.FileOutputWriter;

public class TestIOReaderWriter {
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();

	@Test
	public void test() throws IOException {
		
		String [] lines = new String []{"Hello world", "testline 1", "line2", "last line"};
		File f = folder.newFile("test.txt");
		
		FileOutputWriter fow = new FileOutputWriter(f.getAbsolutePath());
		for(String l:lines){
			fow.writeFileln(l);
		}
		fow.closew();
		
		FileInputReader fir = new FileInputReader(f.getAbsolutePath());
		int count =0;
		String line="";
		while((line=fir.read())!=null){
			assertEquals("line "+count+"read from file ist not equal to line written to file", line, lines[count]);
			count++;
		}
		fir.closer();
	}

}
