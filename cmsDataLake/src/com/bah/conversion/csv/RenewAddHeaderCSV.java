package com.bah.conversion.csv;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class RenewAddHeaderCSV {
	public static final int HEADER_INDEX = 0;
	public static final String DEFAULT_EMPTY_VALUE = "";
	public static final String DEFAULT_TITLE_PREFIX = "UNTITLED_";

	public static void main(String[] args) {
//		File csvFile = new File("C:\\DataLakeGrp\\csv\\2010\\100pct\\sda\\win32\\inp_clm_saf_lds_100_2010.csv");
		
		File csvFile = new File("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\recordcatcher.csv");
		
		try {
			preprocessCSVFile(csvFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/*
	 * Add default title to the untitled columns
	 */
//	private static File preprocessCSVFile(File file) throws IOException {
	private static void preprocessCSVFile(File file) throws IOException {
		List<String[]> rows = readAllRowsFromCSVFile(file);
		/* Remove all the tailing empty string */
//		cleanTailingEmptyStrings(rows);
		
		/* Get the highest column size */
//		int columnSize = validateColumnSize(rows);
		
		/* balance all the rows with the same column size */
//		balanceColumnsSizeOfTheRows(rows, columnSize);
		
		/* Add default title to the untitled header's columns */
		titleHeader(rows);
		
		/* Create a temporary CSV file with the processed CSV contents */
		
//		PrintWriter writer = new PrintWriter("C:\\thunder\\workspace\\jsonDataLake\\pig_src\\JSON100LdsData.cvs","UTF-8");
//		File newcsvFile = new File("C:\\thunder\\workspace\\jsonDataLake\\pig_src\\JSON100LdsData.csv");
		File newcsvFile = new File("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\recordcatcher.csv");
		writeRowToCSVFile(newcsvFile, rows);
/*
		File newcsvFile = new File("C:\\DataLakeGrp\\csv\\2010\\100pct\\sda\\win32\\NEW_inp_clm_saf_lds_100_2010.csv");

		
		File processedFile = createTempCSVFile(newcsvFile);
		writeRowToCSVFile(processedFile, rows, columnSize);
*/		
//		return processedFile;
		System.out.println("...... DONE ....."); 

		return;
	}
	
/*
 * to capture the array size of a data row for the number of headers needed
 */
	private static List<String[]> readAllRowsFromCSVFile(File file) throws IOException {
		System.out.println("...... readAllRowsFromCSVFile ....."); 

//		BufferedReader br = new BufferedReader(new FileReader("C:\\DataLakeGrp\\csv\\2010\\100pct\\sda\\win32\\inp_clm_saf_lds_100_2010.csv"));
		BufferedReader br = new BufferedReader(new FileReader("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\recordcatcher.csv"));
		

		PrintWriter writer = new PrintWriter("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\csvcatcher.csv","UTF-8");

		List<String> lines = new ArrayList<>();
		String line = null;

		int iter = 0;
		while ((line = br.readLine()) != null) {
		    lines.add(line);
		    if(iter > 3) break;
		    iter++;
		}

		
		for(String lineWrite : lines){
//			System.out.println(lineWrite);
			writer.println(lineWrite);
		}

		writer.close();

		File adjCSVFile = new File("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\csvcatcher.csv");
		
		FileReader fileReader = new FileReader(adjCSVFile);
		CSVReader csvReader = new CSVReader(fileReader);
		List<String[]> rows = csvReader.readAll();
		csvReader.close();
		return rows;
	}

	private static void writeRowToCSVFile(File file, List<String[]> rows) throws IOException{
		System.out.println("...... writeRowToCSVFile ....."); 
		writeRowToCSVFile(file, rows, 0);
	}

	private static void writeRowToCSVFile(File file, List<String[]> rows, int columnSize) 
			throws IOException {
			CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
			csvWriter.writeAll(rows);
			csvWriter.close();
		}

	/*
	 * Create the temporary CSV file
	 */
	/*
	private static File createTempCSVFile(File file) throws IOException {

		
		
		String tempCSVFileName = "Preprocessed-" 
			+ FileUtilities.extractFileName(file.getName()) 
			+ "-";
		
		return FileUtilities.createTemporaryFileInDefaultTemporaryDirectory(
					tempCSVFileName, "csv");
		
	}
*/	

	
	private static void titleHeader(List<String[]> rows) {
		System.out.println("...... titleHeader ....."); 
		int numberOfUntitledColumn = 0;
		List<String> lines = new ArrayList<>();
		String[] header = new String[rows.get(HEADER_INDEX).length];
		
		try (BufferedReader brList = new BufferedReader(new FileReader("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\skelton_header.txt"))){

			String line = null;
			while ((line = brList.readLine()) != null) {
			    lines.add(line);
			}
			brList.close();
		}catch (IOException ex){
			ex.printStackTrace();
		}
		if (rows.size() > 0) {
//			String[] header = new String[rows.get(HEADER_INDEX).length];
			if(lines.isEmpty()){
				for (int i = 0; i < header.length; i++) {
					/* Add default title if the column is an empty string */
					header[i] = DEFAULT_TITLE_PREFIX + String.valueOf(numberOfUntitledColumn++);
				}
			}else{
				for (int i = 0; i < header.length; i++) {
					/* Add default title if the column is an empty string */
					try{
						header[i] = lines.get(i);
					}catch(IndexOutOfBoundsException iex){
						break;
					}
				}
				
			}
			rows.add(HEADER_INDEX, header);
			//convert to bytearray
			
			
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream;
			try {
				objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
				objectOutputStream.writeObject(header);
				objectOutputStream.flush();
				objectOutputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			final byte[] byteArray = byteArrayOutputStream.toByteArray();
			
			try {
//				RandomAccessFile r = new RandomAccessFile(new File("C:\\DataLakeGrp\\csv\\2010\\100pct\\sda\\win32\\DataLake.csv"), "rw");
				
				RandomAccessFile r = new RandomAccessFile(new File("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\recordcatcherProjectX.csv"), "rw");
				
//				File newcsvFile = new File("C:\\thunder\\workspace\\jsonDataLake\\pig_src\\JSON100LdsData.csv");
				
//				RandomAccessFile rtemp = new RandomAccessFile(new File("C:\\DataLakeGrp\\csv\\2010\\100pct\\sda\\win32\\DataLake.csv" + "~"), "rw");
				RandomAccessFile rtemp = new RandomAccessFile(new File("C:\\thunder\\workspace\\jsonDataLake\\hive_src\\recordcatcherProjectX.csv" + "~"), "rw");
//				File newcsvFile = new File("C:\\thunder\\workspace\\jsonDataLake\\pig_src\\JSON100LdsData.csv");
				
				long fileSize = r.length();
				System.out.println("size of file = " + fileSize);
				
				FileChannel sourceChannel = r.getChannel();
				FileChannel targetChannel = rtemp.getChannel();
				
				sourceChannel.transferTo(0, (fileSize - 0), targetChannel);
				sourceChannel.truncate(0);
				r.seek(0);
//				r.write(byteArray);
				String headerStr = ""; 
				int intrpos = 0;
				for(String headcol: header){
//					r.writeChars(headcol);
//					r.writeUTF(headcol);
					if(intrpos == 0 ){
						headerStr = headcol;
					}else{
						headerStr = headerStr + "," + headcol;
					}
					intrpos++;
				}
				
				final byte[] headerBytes = headerStr.getBytes(Charset.forName("ISO-8859-1"));
//				final MappedByteBuffer mbf = targetChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
//				r.writeChars(headerStr);
//				r.writeUTF(headerStr);
				r.write(headerBytes);
				
				r.writeChars("\n");

				
				long newOffset = r.getFilePointer();
				targetChannel.position(0L);
				
				sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - 0));
				sourceChannel.close();
				targetChannel.close();
				r.close();
				rtemp.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("-------- DONE with channeling ---------- ");
			
		}
	}

}
