#!/bin/bash

content=$(cat src/main/resources/movie_metadata.csv) 

for i in {1..250}; do
  echo "$content" >> src/main/resources/movie_metadata_big.csv
done
