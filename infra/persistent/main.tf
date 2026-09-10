# Hosted zone already exists — registered via Route 53 with the domain.
data "aws_route53_zone" "main" {
  name = "cardslocal.com"
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

data "aws_db_subnet_group" "main" {
  name = "default-vpc-00c8e1d6f3022c34d"
}

resource "aws_s3_bucket" "images" {
  bucket = "card-marketplace-images-bucket"
}

resource "aws_s3_bucket_cors_configuration" "images" {
  bucket = aws_s3_bucket.images.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["PUT"]
    allowed_origins = [
      "https://app.cardslocal.com",
      "http://localhost:5173"
    ]
    expose_headers = []
  }
}

resource "aws_s3_bucket_policy" "images" {
  bucket = aws_s3_bucket.images.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "AllowCloudFrontOACRead"
      Effect    = "Allow"
      Principal = { Service = "cloudfront.amazonaws.com" }
      Action    = "s3:GetObject"
      Resource  = "${aws_s3_bucket.images.arn}/listings/*"
      Condition = {
        StringEquals = {
          "AWS:SourceArn" = aws_cloudfront_distribution.images.arn
        }
      }
    }]
  })
}

resource "aws_s3_bucket_lifecycle_configuration" "images" {
  bucket = aws_s3_bucket.images.id

  rule {
    id     = "expire-staging-uploads"
    status = "Enabled"

    filter {
      prefix = "staging/"
    }

    expiration {
      days = 1
    }
  }
}


# Distribution and OAC created via console during Phase 6; imported rather than
# recreated to preserve the domain referenced by AWS_CLOUDFRONT_URL and the
# SourceArn condition on the images bucket policy.
resource "aws_cloudfront_origin_access_control" "images" {
  description                       = "Created by CloudFront"
  name                              = "oac-card-marketplace-images-bucket.s3.us-west-2.amaz-msxyuct5v9e"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_distribution" "images" {
  aliases             = []
  comment             = null
  default_root_object = null
  enabled             = true
  http_version        = "http2"
  is_ipv6_enabled     = true
  price_class         = "PriceClass_All"
  retain_on_delete    = false
  staging             = false
  tags = {
    Name = "card-marketplace-images"
  }
  wait_for_deployment = true
  web_acl_id          = "arn:aws:wafv2:us-east-1:323668150901:global/webacl/CreatedByCloudFront-40fffb60/ae839981-0df4-46d8-93c0-7c8374d5aea2"
  default_cache_behavior {
    allowed_methods            = ["GET", "HEAD"]
    cache_policy_id            = "658327ea-f89d-4fab-a63d-7e88639e58f6"
    cached_methods             = ["GET", "HEAD"]
    compress                   = true
    field_level_encryption_id  = null
    origin_request_policy_id   = null
    realtime_log_config_arn    = null
    response_headers_policy_id = null
    smooth_streaming           = false
    target_origin_id           = "card-marketplace-images-bucket.s3.us-west-2.amazonaws.com-msxys3711fc"
    trusted_key_groups         = []
    trusted_signers            = []
    viewer_protocol_policy     = "redirect-to-https"
    grpc_config {
      enabled = false
    }
  }
  origin {
    connection_attempts         = 3
    connection_timeout          = 10
    domain_name                 = aws_s3_bucket.images.bucket_regional_domain_name
    origin_access_control_id    = aws_cloudfront_origin_access_control.images.id
    origin_id                   = "card-marketplace-images-bucket.s3.us-west-2.amazonaws.com-msxys3711fc"
    origin_path                 = null
    response_completion_timeout = 0
  }
  restrictions {
    geo_restriction {
      locations        = []
      restriction_type = "none"
    }
  }
  viewer_certificate {
    acm_certificate_arn            = null
    cloudfront_default_certificate = true
    iam_certificate_id             = null
    minimum_protocol_version       = "TLSv1"
    ssl_support_method             = null
  }
}


resource "aws_acm_certificate" "api" {
  domain_name       = "api.cardslocal.com"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_security_group" "rds" {
  name        = "rds-sg"
  description = "Created by RDS management console"
  vpc_id      = data.aws_vpc.default.id
  tags        = {
    Name = "rds-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "rds_postgres" {
  security_group_id = aws_security_group.rds.id

  cidr_ipv4   = "69.181.139.3/32"
  from_port   = 5432
  to_port     = 5432
  ip_protocol = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "rds_all" {
  security_group_id = aws_security_group.rds.id

  cidr_ipv4 = "0.0.0.0/0"
  ip_protocol       = "-1"
}