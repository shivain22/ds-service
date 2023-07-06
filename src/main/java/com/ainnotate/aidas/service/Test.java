package com.ainnotate.aidas.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		BufferedReader reader;

		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\shiva\\Downloads\\test.txt"));
			String line = reader.readLine();

			int i=1;
			while (line != null) {
				String[] langs = line.split(" ");
				System.out.println("insert into language(id,name)value("+i+",'"+langs[0]+"');");
				// read next line
				line = reader.readLine();
				i++;
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
