package com.example.msdb.Controller;

import com.example.msdb.Entity.Media;
import com.example.msdb.Repository.RepositoryMedia;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media")
@Api(tags = "ControllerMedia", description = "Local SQL primary database APIs")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "The request has succeeded"),
        @ApiResponse(code = 401, message = "The request requires user authentication"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI")})
public class ControllerMedia {

    @Autowired
    RepositoryMedia mediaRepository;


    @GetMapping(value = "/list")
    @ApiOperation(value = "Returns all local medias from primary SQL DB" )
    public Iterable<Media> listPublic() {
        Iterable<Media> medias = mediaRepository.findAll();
        return medias;
    }

}
