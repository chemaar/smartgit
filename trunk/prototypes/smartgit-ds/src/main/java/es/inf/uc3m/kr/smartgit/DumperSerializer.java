package es.inf.uc3m.kr.smartgit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dumpers.GitHubDumper;

public class DumperSerializer {

	protected static Logger logger = Logger.getLogger(DumperSerializer.class);
	public static final String SEPARATOR = ";";

	public static void serialize(GitHubDumper dumper, String file) throws IOException{
		List<Map<Enum, String>> repositories = dumper.createDump();
		write(file, repositories, dumper.getFields());
	}
	
	public static void serialize(GitHubDumper dumper, String file,Map<String,Object> params) throws IOException{
		List<Map<Enum, String>> repositories = dumper.createDump(params);
		write(file, repositories, dumper.getFields());
	}

	public static void write(String file,
			List<Map<Enum, String>> dumpLines, Enum[] fields) throws FileNotFoundException {
			
		if(fields!=null && fields.length>0){
			File f = new File(file);
			boolean exists = f.exists();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new PrintStream(f), StandardCharsets.UTF_8), Boolean.TRUE);
			logger.debug("Writing data to: "+file);
			//1-Create header
			if (!exists){
				logger.debug("Creating header for file: "+file+" with "+fields.length+" fields.");
				for(int i = 0; i<fields.length;i++){
					pw.print(fields[i].name()+SEPARATOR);
				}
				pw.println("");
				pw.flush();
			}else{
				logger.debug("File: "+file+" already exists.");
			}
			//2-Serialize fields
			
			for(Map<Enum, String> line:dumpLines){
				for(int i = 0; i<fields.length;i++){
					pw.print(line.get(fields[i])+SEPARATOR);
				}
				pw.println("");
			}
			
		pw.close();
		}
	
		
	}
}
