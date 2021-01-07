package com.example.msdb.Controller;

import com.example.msdb.Entity.Media;
import com.example.msdb.Service.API.Themoviedb;
import com.example.msdb.elasticsearch.Repository.RepositoryElasticSearchMedia;
import com.example.msdb.elasticsearch.Service.ElasticSearchIndexer;
import io.swagger.annotations.*;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@RestController
@RequestMapping("/elasticsearch")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "The request has succeeded"),
        @ApiResponse(code = 401, message = "The request requires user authentication"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI")})
@Api(tags = "ControllerElasticSearch", description = "Local ElasticSearch secondary database APIs")
public class ControllerElasticSearch {

    @Autowired
    RepositoryElasticSearchMedia elasticSearchMediaRepository;

    @Autowired
    ElasticSearchIndexer elasticSearchIndexer;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    Themoviedb themoviedb;

    @GetMapping(value = "/list")
    @ApiOperation(value = "Returns all local medias from secondary ES DB" )
    public List<Media> list() {
        List<Media> medias = StreamSupport
                .stream(elasticSearchMediaRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return medias;
    }

    @GetMapping(value = "/mediaById")
    @ApiOperation(value = "Returns media filtered by id from ES" )
        public Media mediaById(@ApiParam(defaultValue = "1") @RequestParam int id)  {
        Media media = elasticSearchMediaRepository.findById(id).orElse(null);
        return media;
    }

    @GetMapping(value = "/index")
    @ApiOperation(value = "Index all data from SQL DB to ES" )
    public String index() {
        elasticSearchIndexer.indexData();
        return "Data indexed Successfully";
    }

    @GetMapping(value = "/deindex")
    @ApiOperation(value = "DeIndex all data from ES" )
    public String deindex() {
        elasticSearchMediaRepository.deleteAll();
        return "Deleted all indexes";
    }

    @GetMapping(value = "/search")
    @ApiOperation(value = "Returns a list of movies and shows filtered by inputted query from ES" )
    public List<Media> search(@ApiParam(defaultValue = "Batman") @RequestParam String query) {

        // make query for elasticsearch
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(query)
                        .field("title")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .operator(Operator.AND)
                        .fuzziness(Fuzziness.ONE)
                        .prefixLength(3))
                .build();
        // check elasticsearch
        SearchHits<Media> medias = elasticsearchTemplate.search(searchQuery, Media.class, IndexCoordinates.of("media"));
        double maxScore = medias.getMaxScore();
        long size = medias.getTotalHits();
        // formula for api check
        if (Double.isNaN(maxScore) || maxScore < 0.65 || size < 10){
            themoviedb.search(query, 1);
            medias = elasticsearchTemplate.search(searchQuery, Media.class, IndexCoordinates.of("media"));
        }

        List<Media> mediasResponse = new ArrayList<>();
        if (medias.getTotalHits() > 0)
            mediasResponse = medias.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return mediasResponse;
    }

    @GetMapping(value = "/searchBest")
    @ApiOperation(value = "Returns a only one (the best) movie or show filtered by inputted query from ES" )
    public Media searchBest(@ApiParam(defaultValue = "Matrix") @RequestParam String query) {

        // make query for elasticsearch
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(query)
                        .field("title")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .operator(Operator.AND)
                        .fuzziness(Fuzziness.ONE)
                        .prefixLength(3))
                .build();
        // check elasticsearch
        SearchHit<Media> media = elasticsearchTemplate.searchOne(searchQuery, Media.class, IndexCoordinates.of("media"));
        double maxScore = 0;
        if (media != null)
            maxScore = media.getScore();
        // formula for scoring min api check
        if (Double.isNaN(maxScore) || maxScore <= 1.0){
            themoviedb.search(query, 1);
            media = elasticsearchTemplate.searchOne(searchQuery, Media.class, IndexCoordinates.of("media"));
        }
        Media mediaResponse = new Media();
        if (media != null)
            mediaResponse = media.getContent();
        return mediaResponse;
    }

}
