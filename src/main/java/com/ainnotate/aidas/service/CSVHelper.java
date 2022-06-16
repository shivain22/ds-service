package com.ainnotate.aidas.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CSVHelper {

    public static void main(String[] args) throws IOException, CsvException {
        var fileName = "D:\\Workspaces\\ainnotate-workspace\\aidas-service\\src\\main\\resources\\config\\liquibase\\fake-data\\organisation.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        Path myPath = Paths.get(fileName);
        try (var br = Files.newBufferedReader(myPath,  StandardCharsets.UTF_8);
             var reader = new CSVReaderBuilder(br).withCSVParser(parser).build()) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                for (String e : row) {
                    System.out.format("%s ", e);
                }
                System.out.println();
            }
        }
    }

    public static List<String[]> getData(File file) throws IOException, CsvException, URISyntaxException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        try (var br = Files.newBufferedReader(file.toPath(),  StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser).build()) {
             List<String[]> rows = reader.readAll();
             return rows;
        }
    }

    public static String[] getHeaders(String fileName) throws IOException, CsvException, URISyntaxException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        Path myPath = Path.of(ClassLoader.getSystemResource(fileName).toURI());
        try (var br = Files.newBufferedReader(myPath,  StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser).build()) {
             return reader.readNext();
        }
    }
}
