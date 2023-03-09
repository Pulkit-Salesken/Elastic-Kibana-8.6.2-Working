package com.elk.demo;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class elkConfig {
	

    @Bean
    public ElasticsearchClient elasticsearchClient() {
    	// Create the low-level client
    	RestClient restClient = RestClient.builder(
    	    new HttpHost("localhost", 9200)).build();

    	// Create the transport with a Jackson mapper
    	ElasticsearchTransport transport = new RestClientTransport(
    	    restClient, new JacksonJsonpMapper());

    	// And create the API client
    	ElasticsearchClient client = new ElasticsearchClient(transport);
    	System.out.println(client);
    	try {
			System.out.println(client.nodes().stats());
		} catch (ElasticsearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return client;
    }
    

}
