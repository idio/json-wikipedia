#!/bin/bash
set -e

/var/lib/spark/bin/spark-submit \
    --class it.cnr.isti.hpc.wikipedia.cli.MediawikiToJsonCLI \
    --master local[*] /target/json-wikipedia-jar-with-dependencies.jar \
    -input $1 \
    --output /mnt/data/output_parts \
    --lang $3 \
    --action export-parallel

cat /mnt/data/output_parts/*.jsonl > $2
