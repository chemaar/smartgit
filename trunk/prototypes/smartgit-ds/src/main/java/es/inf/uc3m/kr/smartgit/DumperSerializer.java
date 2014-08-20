package es.inf.uc3m.kr.smartgit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.dumpers.GitHubDumper;

public class DumperSerializer {

	public static final String SEPARATOR = ";";

	public static void serialize(GitHubDumper dumper, String file) throws IOException{
		List<Map<Enum, String>> repositories = dumper.createDump();
		write(file, repositories, dumper.getFields());
	}
	
	public static void serialize(GitHubDumper dumper, String file,Map<String,Object> params) throws IOException{
		List<Map<Enum, String>> repositories = dumper.createDump(params);
		write(file, repositories, dumper.getFields());
	}

	private static void write(String file,
			List<Map<Enum, String>> dumpLines, Enum[] fields) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(file));
		//1-Create header
		for(int i = 0; i<fields.length;i++){
			pw.print(fields[i].name()+SEPARATOR);
		}
		//2-Serialize fields
		pw.println("");
		for(Map<Enum, String> line:dumpLines){
			for(int i = 0; i<fields.length;i++){
				pw.print(line.get(fields[i])+SEPARATOR);
			}
			pw.println("");
		}
		pw.close();
	}
}
