# Movie and Show database backend api
## H2 primary DB and ElasticSearch secondary DB for searching hits

Backend rest API search which query's the database for existing data and throw results for a movie or a show which matches the name with our search term.
If a movie or a show is not found locally, backend will search for the movie/show online using one of the
available resources:
#### https://developers.themoviedb.org/3/getting-started/introduction
When a new movie/show is found, the data of the movie is stored in a local
databases

# Docker setup before project run/debug
cd Docker/Elasticsearch/

# create docker image
sudo docker build -t elasticsearch-image .

# run docker elasticsearch container
sudo docker run --name elasticsearch-container -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch-image

# stop docker container
sudo docker stop elasticsearch-container


# create docker image
sudo docker build -t kibana-image .

# run docker kibana container
sudo docker run --name kibana-container --link elasticsearch-container:elasticsearch -d -p 5601:5601 kibana-image

# Kibana url 
http://localhost:5601/

# Run the project 
## tested with java 12, but there should be no issues with any java 8+ version

# Swagger url
http://localhost:8080/swagger-ui/