package com.elk.demo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TString;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.io.UnsupportedEncodingException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class semanticSearch {
	public String modelPath;
	public SavedModelBundle savedModelBundle;

	static String indexName = "vector_index";

	private static ElasticsearchClient client;

	public semanticSearch(ElasticsearchClient client, String modelPath) {
		super();
		this.client = client;
		this.modelPath = modelPath;
		this.savedModelBundle = SavedModelBundle.load(modelPath, "serve");
	}

	public float[][] embed(String[] values) throws UnsupportedEncodingException {

		// conversion to bytes Tensor
		byte[][] input = new byte[values.length][];
		for (int i = 0; i < values.length; i++) {
			String val = values[i];
			input[i] = val.getBytes(StandardCharsets.UTF_8);

		}
		Tensor<TString> t = TString.tensorOfBytes(NdArrays.vectorOfObjects(input));

		// conversion with Use
		Tensor<TFloat32> result = this.savedModelBundle.session().runner().feed("input", t).fetch("output").run().get(0)
				.expect(TFloat32.DTYPE);

		float[][] output = new float[values.length][512];
		// conversion to regular float array
		long[] idx = new long[2];
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[0].length; j++) {
				idx[0] = i;
				idx[1] = j;
				output[i][j] = result.data().getFloat(idx);
			}
		}

		return output;
	}

	public void searchQuery() throws ElasticsearchException, IOException {
		String query[] = new String[] { "How are you" };

		float[][] vectors = embed(query);

		JSONObject json = new JSONObject();
		json.put("vectors", new JSONArray(Arrays.asList(vectors)));

		ArrayList<Double> list = new ArrayList<>();

		for (int j = 0; j < vectors[0].length; j++) {
			list.add((double) vectors[0][j]);
		}

		System.out.println(list);

		double searchVectors[] = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			searchVectors[i] = list.get(i);
		}

		// This rest client works for the GET request.
		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();

//		JSONObject scriptScoreQuery = new JSONObject();
//		scriptScoreQuery.put("query", new JSONObject()
//		        .put("script_score", new JSONObject()
//		                .put("query", new JSONObject()
//		                        .put("match_all", new JSONObject()))
//		                .put("script", new JSONObject()
////		                        .put("source", "cosineSimilarity(params.query_vector, doc['vecs']) + 1.0")
////		                        .put("params", new JSONObject()
//		                                .put("query_vector", searchVectors))));

//		JSONObject scriptScoreQuery = new JSONObject();
//		scriptScoreQuery.put("query", new JSONObject()
//		        .put("match_all", new JSONObject()));

		// Build the script for the search
//		Map<String, Object> params = new HashMap<>();
//		params.put("query_vector", searchVectors);

		// Build the search request

		// working query
		JSONObject scriptScoreQuery = new JSONObject();
		scriptScoreQuery
				.put("query", new JSONObject().put("bool", new JSONObject().put("must", new JSONArray()
						.put(new JSONObject().put("match_all", new JSONObject()))
						.put(new JSONObject().put("script_score", new JSONObject()
								.put("query", new JSONObject().put("match_all", new JSONObject()))
								.put("script", new JSONObject()
										.put("source", "cosineSimilarity(params.query_vector, 'embedding') + 1.0")
										.put("params", new JSONObject().put("query_vector", searchVectors))))))))
				.put("sort", new JSONArray().put(new JSONObject().put("_score", new JSONObject().put("order", "desc"))))
				.put("size", 3);

		// Define the search request
		HttpEntity entity = new NStringEntity(scriptScoreQuery.toString(), ContentType.APPLICATION_JSON);
		Request searchRequest = new Request("POST", "/my_new_vector_index/_search");
		searchRequest.setEntity(entity);

		Response searchResponse = restClient.performRequest(searchRequest);
		

		JSONObject responseBody = new JSONObject(EntityUtils.toString(searchResponse.getEntity()));
		JSONArray hits = responseBody.getJSONObject("hits").getJSONArray("hits");
		for (int i = 0; i < hits.length(); i++) {
			JSONObject hit = hits.getJSONObject(i);
//		    String documentName = hit.getJSONObject("_source").getString("Document_name");
			System.out.println("Found document: " + hit.toString());
		}

		restClient.close();

//		
//		client.putScript(r -> r
//			    .id("1") 
//			    .script(s -> s
//			        .lang("mustache")
//			        .source("{" +
//               "  \"query\": {" +
//               "    \"script_score\": {" +
//               "      \"script\": {" +
//               "        \"source\": \"cosineSimilarity(params.query_vector, 'my_dense_vector') + 1.0\"," +
//               "        \"params\": {" +
//               "          \"query_vector\": [4, 3.4, -0.2]" +
//               "        }" +
//               "      }" +
//               "    }" +
//               "  }" +
//               "}")
//			        .source("{\"query\":{\"match\":{\"{{field}}\":\"{{value}}\"}}}")
//			    ));

	}

	public void runSearch() throws IOException {

		searchQuery();

//		String[] convertedVectors = new String[] {"Hello", "How are you", "Are you fine", "What are you doing", "How you've been", "What are you doing nowadays"};
//
//		try {
//			float[][] vectors = embed(convertedVectors);
//			
//			JSONObject json = new JSONObject();
//			json.put("vectors", new JSONArray(Arrays.asList(vectors)));
//			
//			
//			int index = 0;
//			
//			double[] newVector = new double[512];
//			for (int y = 0; y < vectors.length; y++) {
//				ArrayList<Double> temp = new ArrayList<>();
//			    for (int j = 0; j < vectors[0].length; j++) {
//			        temp.add((double) vectors[y][j]);
//			    }
//			    
//			    
//			    for(int x = 0;x<temp.size();x++) {
//			    	newVector[x] = temp.get(x);
//			    }
//			    
//				System.out.println("double vector ==> " + newVector);
//				
//				vectorData data = new vectorData();
//				data.setId(1);
//				data.setSentence(convertedVectors[index]);
//				data.setEmbedding(newVector);
//				
//				IndexResponse response = client.index(i -> i
//					    .index("my_new_vector_index")
//					    .document(data)
//					);
//						  
//				
//				System.err.println(response);
//				
//				
//					System.out.println("json data ----->>> " + json);
//					
//					System.out.println("vectors ==> " + newVector[5]);
//			    
//					index++;
//					
//			}
//
////IndexResponse response = Client.index(request);
//
//			System.out.println(Arrays.deepToString(vectors).replace("], ", "]\n"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}
}
