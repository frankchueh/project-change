package com.example.project_ver1;

import java.io.*;


public class FileManager {

	private FileReader reader;
	private FileWriter writer;
	private FileOutputStream fos;
	private File f;
	public String savePath;
	private BufferedReader br;
	FileManager(String savePath)
	{	try{
		this.savePath = savePath;
		f = new File(savePath);
		if(!f.exists())
		{
			f.createNewFile();
		}

		}
		catch(Exception e)
		{
		e.printStackTrace();
		}
	}
	

	
	public String[] readAllLine()
	{	
		String temp,line="";
		try{
			
			reader = new FileReader(savePath);
			br = new BufferedReader(reader);
			
			while((temp=br.readLine())!=null)
			{
				line+=temp+"\n";
			}
			br.close();
			reader.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return line.split("\n");
	}
	
	public void writeAllLine(String[] data)
	{	
		String line="";
		for(int i=0;i<data.length;i++)
		{
			line+=data[i]+"\r\n";
		}
		try{
			writer = new FileWriter(savePath);
			writer.write(line);
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeLine(String data)
	{
		String[] origin = readAllLine();
		String line="";
		if(!(origin.length==1&&origin[0].equals("")))
		{
			for(int i=0;i<origin.length;i++)
			{
				line+=origin[i]+"\r\n";
			}
		}
		
		line+=data+"\r\n";
		try{
			writer = new FileWriter(savePath);
			writer.write(line);
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeObjec(byte[] buffer) throws IOException
	{
		try {
			fos = new FileOutputStream(savePath);
			fos.write(buffer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Clear()
	{
		try {
			writer = new FileWriter(savePath);
			writer.write("");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
//	public static void main(String[] args) {
//			FileManager f = new FileManager();
//			String[] s = f.readAllLine();
//			f.writeLine("いゅ代刚");
//			f.writeLine("いゅ代刚");
//			f.writeLine("いゅ代刚");
//			f.writeLine("いゅ代刚");
//			for(int i=0;i<s.length;i++)
//			{
//				System.out.println(s[i]);
//			}
//			
//	}
	
	
}
