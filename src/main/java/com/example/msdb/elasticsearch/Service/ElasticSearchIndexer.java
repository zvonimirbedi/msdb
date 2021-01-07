package com.example.msdb.elasticsearch.Service;

import com.example.msdb.Entity.Media;
import com.example.msdb.Repository.RepositoryMedia;
import com.example.msdb.elasticsearch.Repository.RepositoryElasticSearchMedia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchIndexer {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    RepositoryElasticSearchMedia elasticSearchMediaRepository;

    @Autowired
    RepositoryMedia mediaRepository;

    public void indexData(){
        // start data entity index
        operations.indexOps(Media.class);
        // load data from local db to elasticsearch
        elasticSearchMediaRepository.saveAll(getData());
    }

    private Iterable<Media> getData() {
        List<Media> list = new ArrayList<Media>();
        Iterable<Media> emps = mediaRepository.findAll();
        emps.forEach(list::add);
        return emps;
    }
}
