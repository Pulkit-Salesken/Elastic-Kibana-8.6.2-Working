package com.elk.demo;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@RestController
public class controller {

//    @Autowired
//    private elkService elkService;
//    
	@Autowired
	private ElasticsearchClient client;

	@GetMapping("/home")
	public void func() {
		System.out.println("func ==> ");
	}

	@GetMapping("/index")
	public String insertRecords() throws IOException {
//	        String status = elkService.insertData(data);

		// pushing data onto the index if alrady exists then insert data otherwise
		// create an index and then insert data

		Integer id = 1;

		// INSERTING DATA INTO ELK

//		  elkData data = new elkData();
//		  
//		  ArrayList<Integer> field1 = new ArrayList<>(Arrays.asList(9));
//		  ArrayList<Integer> field2 = new ArrayList<>(Arrays.asList(5, 3, 5, 2, 6, 7, 3)); 
//		  ArrayList<Integer> field3 = new ArrayList<>(Arrays.asList(5, 3, 2, 6, 7, 3, 8, 9, 4)); 
//		  data.setId(1);
//		  
//		  data.setField1(field1);
//		  data.setField2(field2);
//		  data.setField3(field3);
//
//		  IndexResponse response = client.index(i -> i	  
//		    .index("testing_index")
//		    .	(data)
//		   );
//
//		  System.err.println(response);

//		  elkData data2 = new elkData();
//		  
//		  data.setId(12);
//		  data.setField1(new ArrayList<>(Arrays.asList(9)));
//		  data.setField2(new ArrayList<>(Arrays.asList(5, 3, 5, 2, 6, 7, 3)));
//		  data.setField2(new ArrayList<>(Arrays.asList(5, 3, 2, 6, 7, 3, 8, 9, 4)));

		
		// filters
		Query ff1 = MatchQuery.of(m -> m.field("field1").query("20"))._toQuery();

		Query ff2 = MatchQuery.of(m -> m.field("field2").query("2"))._toQuery();

		Query ff3 = MatchQuery.of(m -> m.field("field3").query("5"))._toQuery();

		SearchResponse<elkData> response = client.search(
				s -> s.index("testing_index").query(q -> q.bool(b -> b.should(ff1).must(ff2).must(ff3))),
				elkData.class);

		List<Hit<elkData>> hits = response.hits().hits();
		for (Hit<elkData> hit : hits) {
			elkData dat = hit.source();
			System.out.println(dat.getField1());
			System.out.println(dat.getField2());
			System.out.println(dat.getField3());
		}

		// Getting Document details By Id;
//		  GetResponse<ObjectNode> response = client.get(g -> g
//				    .index("my_new_index") 
//				    .id("qFytvIYBqkWXJhvAT3RU"),
//				    ObjectNode.class      
//				);
//
//				if (response.found()) {
//					ObjectNode json = response.source();
//					
//					System.err.println(json);
//				} else {
//				    System.err.println("Response not found ==> ");
//				}

		// TODO:

//		  SearchResponse<elkData> search = client.search(s -> s
//				  .index("demo") 
//				    .query(q -> q      
//				        .match(t -> t   
//				            .field("first_name")
//				            .field("last_name")
//				            .query("pulkit")
//				            .query("verma")
//				        )
//				    ),
//				    elkData.class);
//
//		  TotalHits total = search.hits().total();
//		  System.err.println(total);

		return response.toString();
	}

}
