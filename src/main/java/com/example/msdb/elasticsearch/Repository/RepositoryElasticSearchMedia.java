package com.example.msdb.elasticsearch.Repository;

import com.example.msdb.Entity.Media;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RepositoryElasticSearchMedia extends ElasticsearchRepository<Media, Integer> {
}
