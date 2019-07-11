import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import utils.ConsoleUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class IndexSearcher {


    private File indexPath;
    private org.apache.lucene.search.IndexSearcher indexSearcher;
    private IndexReader reader;
    private Analyzer analyzer;

    public IndexSearcher(File indexPath) {
        this.indexPath = indexPath;
    }

    public void open() throws IOException {
        if (!this.indexPath.exists()) {
            throw new FileNotFoundException(String.format("Specified directry %s does not exists!", this.indexPath.getName()));
        }
        if (!this.indexPath.isDirectory()) {
            throw new NotDirectoryException(String.format("%s is not a directory!", this.indexPath.getName()));
        }

        this.reader = DirectoryReader.open(FSDirectory.open(Paths.get(this.indexPath.getPath())));
        this.indexSearcher = new org.apache.lucene.search.IndexSearcher(reader);
        this.analyzer = new StandardAnalyzer();
    }


    public void close() throws IOException {
        this.reader.close();
    }

    //Format is term:searchtext,  if it ends with ~ then apply fuzzy search
    public void search(String query) throws MissingParamsException, IOException, ParseException {
        boolean isFuzzy = query.endsWith("~");
        StringTokenizer tokenizer = new StringTokenizer(query, ":");
        String field = tokenizer.nextToken();
        String term = tokenizer.nextToken();
        if (StringUtils.isEmpty(field))
            throw new MissingParamsException("Search field is not specified!");
        if (StringUtils.isEmpty(term))
            throw new MissingParamsException("Query term is not specified!");


        TopDocs docs;
        if (isFuzzy) {
            term = term.replace("~", "");
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, term));
            docs = this.indexSearcher.search(fuzzyQuery, 10);
        } else {
            QueryParser parser = new QueryParser(field, analyzer);
            parser.setAllowLeadingWildcard(true);
            Query q = parser.parse(term);
            docs = this.indexSearcher.search(q, 10);
        }

        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.BLUE);
        try {
            ConsoleUtils.writeLine(String.format("%d docs are found!. ", docs.totalHits));
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                ConsoleUtils.writeLine(String.format("%s -> %s", field, doc.get(field)));
            }
        } finally {
            ConsoleUtils.resetColour();
        }


    }
}
