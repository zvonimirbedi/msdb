package com.example.msdb.Service.API;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.msdb.Entity.Media;
import com.example.msdb.Repository.RepositoryMedia;
import com.example.msdb.elasticsearch.Repository.RepositoryElasticSearchMedia;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class Themoviedb implements InterfaceMediaAPI {

    @Autowired
    RepositoryElasticSearchMedia elasticSearchMediaRepository;

    @Autowired
    RepositoryMedia mediaRepository;

    @Value("${sources.APIs.themoviedb.search.url}")
    private String apiUrl;
    @Value("${sources.APIs.themoviedb.key}")
    private String apiKey;

    public List<Media> search(String query, int page){
        List<Media> medias = new ArrayList<>();
        try {
            URL url = new URL(apiUrl+"?api_key=" + apiKey + "&page=" + page +"&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString()));
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);


            if(con.getResponseCode()== 200) {
                InputStream is = con.getInputStream();
                BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = response.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JsonArray jsonArrayResponse = new JsonParser().parse(sb.toString()).getAsJsonObject().getAsJsonArray("results");

                for (int i = 0; i < jsonArrayResponse.size(); i++) {
                    try {
                        Media newMedia = parseResponseToMedia(jsonArrayResponse.get(i));
                        medias.add(newMedia);
                    } catch (Exception e) {
                        // todo handle bad data from APIs exception
                        e.printStackTrace();
                    }
                }
            }
            saveToRepositories(medias);
        } catch (Exception e) {
            // todo handle bad requests from APIs exception
            e.printStackTrace();
        }
        return medias;
    }

    private void saveToRepositories (List<Media> medias){
        for (Media media : medias) {
            try {
                mediaRepository.save(media);
                elasticSearchMediaRepository.save(media);
            } catch (Exception e) {
                // todo handle constraint exception
            }
        }
    }
    private Media parseResponseToMedia(JsonElement jsonArrayResponseElement) {
        Media newMedia = new Media();
            if (jsonArrayResponseElement.getAsJsonObject().get("media_type").getAsString().equals("movie")) {
                newMedia.setTitle(jsonArrayResponseElement.getAsJsonObject().get("original_title").getAsString());
                newMedia.setMediatype("M");
                newMedia.setDaterelease(getDateFromResponseObject(jsonArrayResponseElement, "release_date"));
            } else if (jsonArrayResponseElement.getAsJsonObject().get("media_type").getAsString().equals("tv")) {
                newMedia.setTitle(jsonArrayResponseElement.getAsJsonObject().get("original_name").getAsString());
                newMedia.setMediatype("S");
                newMedia.setDaterelease(getDateFromResponseObject(jsonArrayResponseElement, "first_air_date"));
            } else {
                return null;
            }
            newMedia.setDatecreate(new Date());
            newMedia.setDateedit(new Date());

            return newMedia;
    }

    private String getDateFromResponseObject(JsonElement jsonArrayResponseElement, String field){
        String date = jsonArrayResponseElement.getAsJsonObject().get(field).getAsString();
        return date;
    }
}
