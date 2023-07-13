package com.es;

import com.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class DocOperation {
    //建立连接
    public RestHighLevelClient getClient(){
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.222.132", 9200, "http")));
        return client;
    }
    //新增文档
    public void index() throws IOException {
        RestHighLevelClient client = getClient();
        // 新增文档 - 请求对象
        IndexRequest request = new IndexRequest();
        // 设置索引及唯一性标识
        request.index("user").id("1001");
        User user = new User("sz", 123, "man");
        //将属性映射成字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(user);
        System.out.println("映射结果"+s);//{"name":"sz","age":123,"sex":"man"}
        // 添加文档数据，数据格式为 JSON 格式
        request.source(s, XContentType.JSON);
        // 客户端发送请求，获取响应对象
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("_index:" + response.getIndex());
        System.out.println("_id:" + response.getId());
        System.out.println("_result:" + response.getResult());
    }
    //更新文档
    public void update() throws IOException {
        RestHighLevelClient client = getClient();
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("user").id("1001");
        updateRequest.doc(XContentType.JSON,"age",999);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("_index:" + updateResponse.getIndex());
        System.out.println("_id:" + updateResponse.getId());
        System.out.println("_result:" + updateResponse.getResult());
    }
    //查询文档
    public void get() throws IOException {
        RestHighLevelClient client = getClient();
        //1.创建请求对象
        GetRequest request = new GetRequest().index("user").id("1000");
        //2.客户端发送请求，获取响应对象
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println("_index:" + response.getIndex());
        System.out.println("_type:" + response.getType());
        System.out.println("_id:" + response.getId());
        System.out.println("source:" + response.getSourceAsString());//{"name":"sz","age":999,"sex":"man"}
    }
    //批量增加文档
    public void bulkAddRequest() throws IOException {
        RestHighLevelClient client = getClient();
        BulkRequest bulkRequest1 = new BulkRequest();
        bulkRequest1.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON,"name","小明","age","123"));
        bulkRequest1.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON,"name","小花","age","121"));
        bulkRequest1.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON,"name","小黑","age","124"));
        BulkResponse responses = client.bulk(bulkRequest1, RequestOptions.DEFAULT);
        System.out.println("took:" + responses.getTook());
        System.out.println("items:" + responses.getItems());
    }
    //批量删除文档
    public void bulkDeleteRequest() throws IOException {
        RestHighLevelClient client = getClient();
        BulkRequest bulkRequest1 = new BulkRequest();
        bulkRequest1.add(new DeleteRequest().index("user").id("1003"));
        BulkResponse responses = client.bulk(bulkRequest1, RequestOptions.DEFAULT);
        System.out.println("took:" + responses.getTook());
        System.out.println("items:" + responses.getItems());
    }

    //查询所有索引数据
    public void matchAllQuery() throws IOException {
        RestHighLevelClient client  = getClient();
        // 创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);
        // 查询匹配
        SearchHits hits = response.getHits();
        System.out.println("hits========>>");
        for (SearchHit hit : hits) {
            //输出每条查询的结果信息
            System.out.println(hit.getSourceAsString());
        }
        System.out.println("<<========");
    }

    public static void main(String[] args) throws IOException {

//        new DocOperation().index();
//        new DocOperation().update();
//        new DocOperation().get();
//        new DocOperation().bulkAddRequest();
//        new DocOperation().bulkDeleteRequest();
        new DocOperation().matchAllQuery();
    }
}
