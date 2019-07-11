import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import utils.ConsoleUtils;
import utils.CsvImporter;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer {
    private enum FieldType {
        INDEX_FIELD,
        ID_FIELD
    }
    private static class FieldLookup {
        public String fieldName;
        public int  index;
        public FieldType fieldType;
    }




    private static void createIndexPath(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
    }

    public static void createIndex(final ApplicationParams params) throws IOException {
        ConsoleUtils.writeLine(String.format("Creating index %s for the data file %s", params.pathOfIndex, params.dataFileFullName));
        createIndexPath(params.pathOfIndex);
        Directory directory = FSDirectory.open(Paths.get(params.pathOfIndex));
        try {
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setCommitOnClose(true);
            IndexWriter writer = new IndexWriter(directory, config);
            try {

                CsvImporter csvImporter = new CsvImporter(new File(params.dataFileFullName));
                csvImporter.open();
                Map<String, Integer> headerMap =  csvImporter.getHeaderMap();
                final FieldLookup[] lookupTable = new FieldLookup[params.csvIdFields.size() + params.csvIndexFields.size()];
                int i = 0;
                for(String s : params.csvIdFields){
                    FieldLookup fieldLookup =  new FieldLookup();
                    fieldLookup.fieldName = s;
                    fieldLookup.fieldType = FieldType.ID_FIELD;
                    fieldLookup.index = headerMap.get(s);
                    lookupTable[i] = fieldLookup;
                    i++;
                }
                for(String s : params.csvIndexFields){
                    FieldLookup fieldLookup =  new FieldLookup();
                    fieldLookup.fieldName = s;
                    fieldLookup.fieldType = FieldType.INDEX_FIELD;
                    fieldLookup.index = headerMap.get(s);
                    lookupTable[i] = fieldLookup;
                    i++;
                }

                try {
                    csvImporter.readCsvFile(record -> {
                        Document doc = new Document();
                        IndexableField field = null;
                        for(FieldLookup fieldLookup : lookupTable){
                            switch (fieldLookup.fieldType){
                                case ID_FIELD:
                                    field = new StringField(fieldLookup.fieldName, record.get(fieldLookup.index), Field.Store.YES);
                                    break;
                                case INDEX_FIELD:
                                    field = new TextField(fieldLookup.fieldName, record.get(fieldLookup.index), Field.Store.YES);
                                    break;
                            }
                            doc.add(field);
                        }
                        writer.addDocument(doc);
                        if (record.getRecordNumber() % 10000 == 0)
                            ConsoleUtils.write(".");
                        return true;
                    });
                }
                finally{
                    csvImporter.close();
                }
            }
            finally {
                writer.close();
            }
        }finally {
            directory.close();
        }

        ConsoleUtils.writeLine(String.format("\nSUCCESSFULY CREATED index %s for the data file %s", params.pathOfIndex, params.dataFileFullName));

    }

}
