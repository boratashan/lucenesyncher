package abandon.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElasticClient {
    private RestHighLevelClient client;

    public void open() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

    }

    public void close() throws IOException {
        if (client!=null)
        client.close();
    }


    public void indexCountries() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("countries3");
        client.indices().create(request);
    }

    public void indexCountry(int countryID, String countryName, String countryCode) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("CountryName", countryName);
        jsonMap.put("CountryCode", countryCode);

        try {

            IndexRequest request = new IndexRequest("countries3", "doc", Integer.toString(countryID));
            request.source(jsonMap);
            IndexResponse indexResponse = client.index(request);
            System.out.println(String.format("Index country status -> %s", indexResponse.toString()));
        }
        catch (Exception e ) {
            System.out.println(e.getMessage());
        }

    }


    public void search() {
        
    }
}
