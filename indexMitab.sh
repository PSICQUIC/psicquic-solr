#!/bin/bash

if [ $# == 2 ];
then
      MITAB_FILE=$1;
      SOLR_URL=$2;
      echo "MITAB file: ${MITAB_FILE}"
      echo "SOLR server URL: ${SOLR_URL}"
      mvn clean install -Pexec -Dmitab.file=${MITAB_FILE} -Dsolr.url=${SOLR_URL} -Dmaven.test.skip
elif [ $# == 1 ];
then
      MITAB_FILE=$1;
      echo "MITAB file: ${MITAB_FILE}"
      echo "SOLR server URL not provided, use default: ${SOLR_URL}"
      mvn clean install -Pexec -Dmitab.file=${MITAB_FILE} -Dmaven.test.skip
else
      echo ""
      echo "ERROR: wrong number of parameters ($#)."
      echo "usage: MITAB_FILE (SOLR_URL)"
      echo "usage: MITAB_FILE: the name of the MITAB file to index. Can be 2.5, 2.6 or 2.7"
      echo "usage: SOLR_URL: the url of the SOLR server file (By default, if nothing is given, it is : http://localhost:8983/solr)"
      echo ""
      exit 1
fi