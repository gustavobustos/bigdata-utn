package ar.bigdata.analisis.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	
	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
	
	public static void saveFile (String fileName, List<String> lines) {
		
		try {
			File fout = new File(fileName);
			FileOutputStream fos = new FileOutputStream(fout);
		 
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			for (String line : lines) {
				
				bw.write(line);
			}
			
			bw.close();
		} catch (IOException ioException) {
			log.error("IO: ",ioException);
		}
			
		
	}

}
