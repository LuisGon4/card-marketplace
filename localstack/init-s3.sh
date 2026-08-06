#!/bin/bash
awslocal s3 mb s3://${AWS_S3_BUCKET}

awslocal s3api put-bucket-cors --bucket ${AWS_S3_BUCKET} --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["http://localhost:5173"],
      "AllowedMethods": ["PUT", "GET"],
      "AllowedHeaders": ["*"],
      "ExposeHeaders": ["ETag"],
      "MaxAgeSeconds": 3000
    }
  ]
}'