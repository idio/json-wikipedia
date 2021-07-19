#!/bin/bash
set -e

/var/lib/spark/bin/spark-submit \
    --class it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonCLI \
    --driver-memory $4 \
    --master local[*] \
     /target/json-wikipedia-1.2.0-jar-with-dependencies.jar \
    -input $1 \
    --output /mnt/data/output_parts \
    --lang $3 \
    --action export-parallel \
    
mkdir -p  $(dirname "$2")
cat /mnt/data/output_parts/part-* > $2
