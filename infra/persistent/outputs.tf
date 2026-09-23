output "rds_sg_id" {
  value = aws_security_group.rds.id
}

output "certificate_arn" {
  value = aws_acm_certificate.api.arn
}

output "rds_endpoint" {
  value = aws_db_instance.db.endpoint
}

output "cloudfront_domain" {
  value = aws_cloudfront_distribution.images.domain_name
}

output "images_bucket_name" {
  value = aws_s3_bucket.images.bucket
}