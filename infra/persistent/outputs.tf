output "rds_sg_id" {
  value = aws_security_group.rds.id
}

output "certificate_arn" {
  value = aws_acm_certificate.api.arn
}