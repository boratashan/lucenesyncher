package utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.*;
import java.util.Map;

public class CsvImporter {

    private BufferedReader bufferedReader;
    private CSVParser csvParser;
    private File csvFile;

    @FunctionalInterface
    public interface Traverser {
        public boolean onReadRecords(CSVRecord record) throws IOException;

    }

    public CsvImporter(File csvFile) {
        this.csvFile = csvFile;
    }

    public void open() throws IOException {
        bufferedReader = new BufferedReader(new FileReader(csvFile));

        CSVFormat csvFormat = CSVFormat.newFormat(';').withDelimiter(';')
                .withFirstRecordAsHeader()
                .withQuote('"')
                .withQuoteMode(QuoteMode.ALL)
                .withSkipHeaderRecord();
        csvParser = new CSVParser(bufferedReader, csvFormat);
    }

    public void close() throws IOException {
        if (!csvParser.isClosed())
            csvParser.close();
        bufferedReader.close();
    }

    public Map<String, Integer> getHeaderMap() {
        return csvParser.getHeaderMap();
    }


    public void readCsvFile(final Traverser traverser) throws IOException {
        for (CSVRecord record : csvParser) {
            if (!traverser.onReadRecords(record))
                break;
        }
    }
}
