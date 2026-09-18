data "aws_caller_identity" "current" {}

data "aws_vpc" "default" {
  default = true
}

data "terraform_remote_state" "persistent" {
  backend = "s3"
  config = {
    bucket       = "cardslocal-tfstate-b4a8"
    key          = "persistent/terraform.tfstate"
    region       = "us-west-2"
  }
}

data "aws_iam_role" "execution" {
  name = "card-marketplace-ecs-execution-role"
}

data "aws_iam_role" "task" {
  name = "card-marketplace-ecs-task-role"
}

data "aws_cloudwatch_log_group" "app" {
  name = "/ecs/card-marketplace"
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_ecs" {
  security_group_id            = data.terraform_remote_state.persistent.outputs.rds_sg_id
  referenced_security_group_id = aws_security_group.ecs.id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
}

resource "aws_security_group" "alb" {
  name        = "alb-sg"
  description = "Security group for alb"
  vpc_id      = data.aws_vpc.default.id
  tags        = {
    Name = "alb-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "alb_in" {
  security_group_id = aws_security_group.alb.id

  from_port         = 443
  to_port           = 443
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "alb_out" {
  security_group_id = aws_security_group.alb.id
  referenced_security_group_id = aws_security_group.ecs.id

  from_port         = 8080
  to_port           = 8080
  ip_protocol       = "tcp"

}

resource "aws_security_group" "ecs" {
  name        = "ecs-sg"
  description = "Security group for ecs"
  vpc_id      = data.aws_vpc.default.id
  tags        = {
    Name = "ecs-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ecs_in" {
  security_group_id = aws_security_group.ecs.id
  referenced_security_group_id = aws_security_group.alb.id

  from_port         = 8080
  to_port           = 8080
  ip_protocol       = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_rds" {
  security_group_id            = aws_security_group.ecs.id
  referenced_security_group_id = data.terraform_remote_state.persistent.outputs.rds_sg_id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_elasticache" {
  security_group_id = aws_security_group.ecs.id
  referenced_security_group_id = aws_security_group.elasticache.id

  from_port                    = 6379
  to_port                      = 6379
  ip_protocol                  = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_internet" {
  security_group_id = aws_security_group.ecs.id
  cidr_ipv4          = "0.0.0.0/0"
  from_port          = 443
  to_port            = 443
  ip_protocol        = "tcp"
}

resource "aws_security_group" "elasticache" {
  name        = "elasticache-sg"
  description = "Security group for elasticache"
  vpc_id      = data.aws_vpc.default.id
  tags        = {
    Name = "elasticache-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "elasticache_in" {
  security_group_id            = aws_security_group.elasticache.id
  referenced_security_group_id = aws_security_group.ecs.id

  from_port                    = 6379
  to_port                      = 6379
  ip_protocol                  = "tcp"
}
