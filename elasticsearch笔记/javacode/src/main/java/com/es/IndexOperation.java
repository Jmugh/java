package com.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;

public class IndexOperation {
    public  RestHighLevelClient getClient(){
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.222.132", 9200, "http")));
        return client;
    }
    public  void createIndex() throws IOException {
        RestHighLevelClient client = getClient();
        //请求对象
        CreateIndexRequest request = new CreateIndexRequest("user");
        //发送请求 返回响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println("操作状态"+response.isAcknowledged());
    }
    public void getIndex() throws IOException {
        RestHighLevelClient client = getClient();
        GetIndexRequest request = new GetIndexRequest("user");
        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
        System.out.println("mappings"+response.getMappings());
        System.out.println("aliases"+response.getAliases());
    }
    public void deleteIndex() throws IOException {
        RestHighLevelClient client = getClient();
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println("删除索引"+request.toString());
    }
    public static void main(String[] args) throws IOException {
//        new IndexOperation().createIndex();
        new IndexOperation().getIndex();
        new IndexOperation().deleteIndex();

    }
}
