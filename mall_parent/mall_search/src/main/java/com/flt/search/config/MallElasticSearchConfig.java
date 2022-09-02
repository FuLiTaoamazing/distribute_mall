package com.flt.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MallElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("10.15.0.3", 9200, "http")));
        return client;
    }


    public static RequestOptions defaultOptions() {
        RequestOptions aDefault = RequestOptions.DEFAULT;
        return aDefault;
    }
}
