package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


public class CountriesIndexer {

    private String indexDirectory = Constants.INDEX_DIRECTORY;

    private Directory directory;
    private IndexWriter writer;
    private IndexReader reader;
    private Analyzer analyzer;
    private IndexSearcher indexSearcher;
    private QueryParser queryParser;
    private Query query;

    public void open() throws IOException {
        File indexFile = new File(indexDirectory);
        this.directory = FSDirectory.open(Paths.get(indexDirectory));
        this.analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        this.writer = new IndexWriter(directory, config);

    }

    public void close() throws IOException {
        this.writer.close();
        this.directory.close();
    }


    public void addCountryToIndex(Country country) throws IOException {
        Document document = new Document();
        document.add(new StringField("id", country.getId(), Field.Store.YES));
        document.add(new TextField("countryName", country.getCountryName(), Field.Store.YES));
        document.add(new StringField("countryCode", country.getCountryCode(), Field.Store.YES));
        writer.addDocument(document);
    }

    public void search() throws ParseException, IOException {
       /* this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectory)));

        indexSearcher = new IndexSearcher(reader);
        this.analyzer = new StandardAnalyzer();
        queryParser = new QueryParser("countryName", analyzer);


        Query q = queryParser.parse("ger*");
        TopDocs topDocs = indexSearcher.search(q, 100000);
        Document d =  indexSearcher.doc(2);

        System.out.println(d.get("countryName"));
        System.out.println(topDocs.scoreDocs.length);

*/
        Directory dir = FSDirectory.open(Paths.get(indexDirectory)); //3
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectory)));
        IndexSearcher is = new IndexSearcher(reader);   //3

        QueryParser parser = new QueryParser( // 4
                "countryName",  //4
                new StandardAnalyzer());  //4
        Query query = parser.parse("g*");              //4
        FuzzyQuery fq = new FuzzyQuery(new Term("countryName", "Gemrany"));
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(fq, 10); //5
        long end = System.currentTimeMillis();

        System.err.println("Found " + hits.totalHits +   //6
                " document(s) (in " + (end - start) +        // 6
                " milliseconds) that matched query '" +     // 6
                "ger*" + "':");                                   // 6

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get("countryName"));  //8
        }



    }


}
