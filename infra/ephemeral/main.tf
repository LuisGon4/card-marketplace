data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

data "aws_vpc" "default" {
  default = true
}

data "aws_secretsmanager_secret" "app" {
  name = "card-marketplace/prod"
}

data "terraform_remote_state" "persistent" {
  backend = "s3"
  config = {
    bucket = "cardslocal-tfstate-b4a8"
    key    = "persistent/terraform.tfstate"
    region = "us-west-2"
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

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
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
  tags = {
    Name = "alb-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "alb_in" {
  security_group_id = aws_security_group.alb.id

  from_port   = 443
  to_port     = 443
  cidr_ipv4   = "0.0.0.0/0"
  ip_protocol = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "alb_out" {
  security_group_id            = aws_security_group.alb.id
  referenced_security_group_id = aws_security_group.ecs.id

  from_port   = 8080
  to_port     = 8080
  ip_protocol = "tcp"

}

resource "aws_security_group" "ecs" {
  name        = "ecs-sg"
  description = "Security group for ecs"
  vpc_id      = data.aws_vpc.default.id
  tags = {
    Name = "ecs-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ecs_in" {
  security_group_id            = aws_security_group.ecs.id
  referenced_security_group_id = aws_security_group.alb.id

  from_port   = 8080
  to_port     = 8080
  ip_protocol = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_rds" {
  security_group_id            = aws_security_group.ecs.id
  referenced_security_group_id = data.terraform_remote_state.persistent.outputs.rds_sg_id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_elasticache" {
  security_group_id            = aws_security_group.ecs.id
  referenced_security_group_id = aws_security_group.elasticache.id

  from_port   = 6379
  to_port     = 6379
  ip_protocol = "tcp"
}

resource "aws_vpc_security_group_egress_rule" "ecs_out_internet" {
  security_group_id = aws_security_group.ecs.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
}

resource "aws_security_group" "elasticache" {
  name        = "elasticache-sg"
  description = "Security group for elasticache"
  vpc_id      = data.aws_vpc.default.id
  tags = {
    Name = "elasticache-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "elasticache_in" {
  security_group_id            = aws_security_group.elasticache.id
  referenced_security_group_id = aws_security_group.ecs.id

  from_port   = 6379
  to_port     = 6379
  ip_protocol = "tcp"
}

resource "aws_ecs_task_definition" "app" {
  family                   = "card-marketplace"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = data.aws_iam_role.execution.arn
  task_role_arn            = data.aws_iam_role.task.arn

  runtime_platform {
    cpu_architecture        = "ARM64"
    operating_system_family = "LINUX"
  }
  container_definitions = jsonencode([
    {
      name      = "card-marketplace",
      image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${data.aws_region.current.name}.amazonaws.com/card-marketplace:${var.image_tag}"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      environment = [
        { name = "APP_FRONTEND_URL", value = "https://app.cardslocal.com" },
        { name = "POSTGRES_DATASOURCE_URL", value = "..." },
        { name = "SPRING_DATA_REDIS_HOST", value = "..." },
        { name = "SPRING_DATA_REDIS_PORT", value = "6379" },
        { name = "JUSTTCG_BASE_URL", value = "..." },
        { name = "AWS_S3_REGION", value = data.aws_region.current.name },
        { name = "AWS_S3_BUCKET", value = "card-marketplace-images-bucket" },
        { name = "AWS_CLOUDFRONT_URL", value = "..." },
        { name = "SPRING_PROFILES_ACTIVE", value = "prod, seed" },
      ]

      secrets = [
        { name = "POSTGRES_NAME", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:POSTGRES_NAME::" },
        { name = "POSTGRES_PASSWORD", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:POSTGRES_PASSWORD::" },
        { name = "SPRING_DATA_REDIS_PASSWORD", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:SPRING_DATA_REDIS_PASSWORD::" },
        { name = "JUSTTCG_API_KEY", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:JUSTTCG_API_KEY::" },
        { name = "GOOGLE_CLIENT_ID", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:GOOGLE_CLIENT_ID::" },
        { name = "GOOGLE_CLIENT_SECRET", valueFrom = "${data.aws_secretsmanager_secret.app.arn}:GOOGLE_CLIENT_SECRET::" },
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = data.aws_cloudwatch_log_group.app.name
          "awslogs-region"        = data.aws_region.current.name
          "awslogs-stream-prefix" = "ecs"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "app" {
  name            = "card-marketplace"
  cluster         = ""
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [data.aws_subnets.default.ids]
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = "card-marketplace"
    container_port   = 8080
  }
}

resource "aws_ecs_cluster" "main" {
  name = "card-marketplace"

  tags = {
    Name = "card-marketplace-cluster"
  }
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.arn
  capacity_providers = ["FARGATE"]
}

resource "aws_alb" "app" {
  name               = "card-marketplace-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = [data.aws_subnets.default.ids]

  tags = {
    Name = "card-marketplace-alb"
  }
}

resource "aws_alb_target_group" "app" {
  name        = "card-marketplace-tg"
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = data.aws_vpc.default.id

  health_check {
    path                = "/health"
    protocol            = "HTTP"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }

  tags = {
    Name = "card-marketplace-tg"
  }
}

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_alb.app.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = data.terraform_remote_state.persistent.outputs.certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.app.arn
  }
}