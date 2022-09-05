package com.ainnotate.aidas.service;

import com.ainnotate.aidas.domain.UploadMetaData;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {

    public static void main(String[] args) throws IOException, CsvException {
        var fileName = "D:\\Workspaces\\ainnotate-workspace\\aidas-service\\src\\main\\resources\\config\\liquibase\\fake-data\\organisation.csv";
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        Path myPath = Paths.get(fileName);
        try (var br = Files.newBufferedReader(myPath, StandardCharsets.UTF_8);
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
        try (var br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser).build()) {
            List<String[]> rows = reader.readAll();
            return rows;
        }
    }

    public static String[] getHeaders(String fileName) throws IOException, CsvException, URISyntaxException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        Path myPath = Path.of(ClassLoader.getSystemResource(fileName).toURI());
        try (var br = Files.newBufferedReader(myPath, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(br).withCSVParser(parser).build()) {
            return reader.readNext();
        }
    }

    public static ByteArrayInputStream uploadMetaDataToCsv(List<UploadMetaData> uploadMetaDatas) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (UploadMetaData uploadMetaData : uploadMetaDatas) {
                List<String> data = Arrays.asList(
                    String.valueOf(uploadMetaData.getId()),
                    uploadMetaData.getProjectProperty()!=null ? uploadMetaData.getProjectProperty().getProperty().getName():"",
                    uploadMetaData.getObjectProperty()!=null ? uploadMetaData.getObjectProperty().getProperty().getName():"",
                    uploadMetaData.getValue()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
