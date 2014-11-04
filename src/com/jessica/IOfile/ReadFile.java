package com.jessica.IOfile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {

	BufferedReader br;
	/**
	 * Opens file referred to by the filename
	 * @param filename file to be open and read
	 */
	public ReadFile(String filename) {
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open file");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Gets the next line from the file; if no lines are left, returns
	 * null.
	 * @return the next line unless no lines exist then it returns null
	 */
	public String getNext() {
		String currLine;
		try {
			while ((currLine = br.readLine()) != null) {
				return currLine;
			}
		} catch (IOException e) {
			
		}
		return null;
	}
	
	public void close() {
		if (br != null)
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		br = null;
	}
	
}
