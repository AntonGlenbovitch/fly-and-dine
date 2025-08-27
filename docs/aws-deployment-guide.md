# AWS Deployment Guide for Inflight Ordering Application

This comprehensive guide provides step-by-step instructions for deploying the Inflight Ordering Application on Amazon Web Services (AWS) infrastructure. The deployment architecture leverages AWS managed services to provide scalability, reliability, and security while minimizing operational overhead and maintenance requirements.

## Architecture Overview

The AWS deployment architecture uses a multi-tier approach with separate layers for presentation, application logic, data storage, and supporting services. This architecture provides high availability, scalability, and security while enabling efficient resource utilization and cost optimization.

### Core Infrastructure Components

**Amazon ECS (Elastic Container Service)** hosts the Java backend application using Docker containers with automatic scaling based on demand. ECS provides container orchestration with integrated load balancing, service discovery, and health monitoring that ensures reliable application availability.

**Amazon RDS (Relational Database Service)** provides managed PostgreSQL database services with automated backups, security patching, and high availability through Multi-AZ deployments. RDS eliminates database administration overhead while providing enterprise-grade reliability and performance.

**Amazon S3 (Simple Storage Service)** stores static web assets, application artifacts, and backup data with high durability and availability. S3 provides cost-effective storage with lifecycle management policies that optimize storage costs over time.

**Amazon CloudFront** provides global content delivery network (CDN) services that improve application performance and reduce latency for users worldwide. CloudFront includes security features such as DDoS protection and SSL/TLS termination.

**Application Load Balancer (ALB)** distributes incoming traffic across multiple application instances with health checking and automatic failover capabilities. ALB provides SSL termination, path-based routing, and integration with AWS security services.

### Security and Compliance Infrastructure

**AWS Identity and Access Management (IAM)** provides fine-grained access control for all AWS resources with role-based permissions that follow the principle of least privilege. IAM includes support for multi-factor authentication and integration with external identity providers.

**AWS Key Management Service (KMS)** manages encryption keys for data protection with automatic key rotation and audit logging. KMS provides FIPS 140-2 validated hardware security modules for cryptographic operations.

**AWS Secrets Manager** securely stores and manages database credentials, API keys, and other sensitive configuration data with automatic rotation capabilities. Secrets Manager integrates with application code to provide secure credential access without hardcoded secrets.

**AWS CloudTrail** provides comprehensive audit logging of all API calls and administrative actions with tamper-evident log storage. CloudTrail enables compliance monitoring and security incident investigation.

**AWS Config** monitors resource configurations and compliance with security policies through automated compliance checking and remediation. Config provides continuous compliance monitoring and alerting for configuration drift.

### Monitoring and Operations Infrastructure

**Amazon CloudWatch** provides comprehensive monitoring and alerting for application performance, infrastructure health, and business metrics. CloudWatch includes custom dashboards, automated alerting, and log aggregation capabilities.

**AWS X-Ray** provides distributed tracing and performance analysis for application troubleshooting and optimization. X-Ray enables identification of performance bottlenecks and error patterns across distributed application components.

**AWS Systems Manager** provides centralized management for configuration, patching, and operational tasks across all infrastructure components. Systems Manager includes automation capabilities for routine maintenance and emergency response procedures.

## Prerequisites and Planning

### AWS Account Setup

Create an AWS account with appropriate billing and support plans that meet your operational requirements and budget constraints. Consider AWS Enterprise Support for production deployments that require 24/7 technical support and dedicated technical account management.

Configure AWS Organizations for multi-account management if deploying across multiple environments or business units. Organizations provides centralized billing, security policy management, and resource sharing across multiple AWS accounts.

Set up AWS Cost and Billing alerts to monitor spending and prevent unexpected charges. Configure budget alerts and spending limits that align with your financial planning and cost management requirements.

### Security and Compliance Preparation

Review and implement AWS security best practices including multi-factor authentication for root accounts, IAM password policies, and CloudTrail logging for all regions. Security preparation includes both technical controls and procedural safeguards.

Assess compliance requirements for your specific industry and geographic regions including data residency requirements, encryption standards, and audit logging requirements. Compliance preparation may require specific AWS services or configurations.

Plan network security architecture including VPC design, subnet allocation, security group rules, and network access control lists (NACLs) that provide appropriate security boundaries and access controls.

### Capacity and Performance Planning

Estimate resource requirements based on expected user load, data volume, and performance requirements. Consider both normal operational load and peak usage scenarios that may occur during high-traffic periods or special events.

Plan for scalability requirements including auto-scaling policies, load balancing strategies, and database scaling approaches that can accommodate growth in usage and data volume over time.

Consider disaster recovery requirements including backup strategies, cross-region replication, and recovery time objectives that meet your business continuity requirements.

## Step-by-Step Deployment Instructions

### Phase 1: Core Infrastructure Setup

#### VPC and Network Configuration

Create a Virtual Private Cloud (VPC) with appropriate CIDR block allocation that provides sufficient IP address space for current and future requirements. Use /16 CIDR blocks for production environments to ensure adequate address space.

```bash
# Create VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=inflight-ordering-vpc}]'

# Create Internet Gateway
aws ec2 create-internet-gateway --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=inflight-ordering-igw}]'

# Attach Internet Gateway to VPC
aws ec2 attach-internet-gateway --vpc-id vpc-xxxxxxxxx --internet-gateway-id igw-xxxxxxxxx
```

Create public and private subnets across multiple Availability Zones for high availability and fault tolerance. Use public subnets for load balancers and NAT gateways, and private subnets for application servers and databases.

```bash
# Create public subnets
aws ec2 create-subnet --vpc-id vpc-xxxxxxxxx --cidr-block 10.0.1.0/24 --availability-zone us-east-1a --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=public-subnet-1a}]'
aws ec2 create-subnet --vpc-id vpc-xxxxxxxxx --cidr-block 10.0.2.0/24 --availability-zone us-east-1b --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=public-subnet-1b}]'

# Create private subnets
aws ec2 create-subnet --vpc-id vpc-xxxxxxxxx --cidr-block 10.0.11.0/24 --availability-zone us-east-1a --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=private-subnet-1a}]'
aws ec2 create-subnet --vpc-id vpc-xxxxxxxxx --cidr-block 10.0.12.0/24 --availability-zone us-east-1b --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=private-subnet-1b}]'
```

Configure NAT Gateways in public subnets to provide internet access for resources in private subnets while maintaining security isolation. NAT Gateways provide managed NAT services with high availability and automatic scaling.

```bash
# Allocate Elastic IPs for NAT Gateways
aws ec2 allocate-address --domain vpc --tag-specifications 'ResourceType=elastic-ip,Tags=[{Key=Name,Value=nat-gateway-1a-eip}]'
aws ec2 allocate-address --domain vpc --tag-specifications 'ResourceType=elastic-ip,Tags=[{Key=Name,Value=nat-gateway-1b-eip}]'

# Create NAT Gateways
aws ec2 create-nat-gateway --subnet-id subnet-xxxxxxxxx --allocation-id eipalloc-xxxxxxxxx --tag-specifications 'ResourceType=nat-gateway,Tags=[{Key=Name,Value=nat-gateway-1a}]'
aws ec2 create-nat-gateway --subnet-id subnet-xxxxxxxxx --allocation-id eipalloc-xxxxxxxxx --tag-specifications 'ResourceType=nat-gateway,Tags=[{Key=Name,Value=nat-gateway-1b}]'
```

#### Security Groups and Network ACLs

Create security groups with least-privilege access rules that allow only necessary traffic between application components. Security groups act as virtual firewalls that control traffic at the instance level.

```bash
# Create security group for Application Load Balancer
aws ec2 create-security-group --group-name alb-security-group --description "Security group for Application Load Balancer" --vpc-id vpc-xxxxxxxxx

# Add rules for ALB security group
aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxx --protocol tcp --port 80 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-id sg-xxxxxxxxx --protocol tcp --port 443 --cidr 0.0.0.0/0

# Create security group for ECS tasks
aws ec2 create-security-group --group-name ecs-security-group --description "Security group for ECS tasks" --vpc-id vpc-xxxxxxxxx

# Add rules for ECS security group (allow traffic from ALB)
aws ec2 authorize-security-group-ingress --group-id sg-yyyyyyyyy --protocol tcp --port 8080 --source-group sg-xxxxxxxxx
```

Configure Network ACLs for additional network-level security controls that provide defense-in-depth protection. Network ACLs operate at the subnet level and provide stateless filtering of network traffic.

### Phase 2: Database Setup

#### RDS PostgreSQL Configuration

Create an RDS subnet group that spans multiple Availability Zones for high availability and automatic failover capabilities. Subnet groups define the subnets where RDS instances can be deployed.

```bash
# Create DB subnet group
aws rds create-db-subnet-group --db-subnet-group-name inflight-ordering-db-subnet-group --db-subnet-group-description "Subnet group for inflight ordering database" --subnet-ids subnet-xxxxxxxxx subnet-yyyyyyyyy
```

Create the RDS PostgreSQL instance with appropriate instance class, storage configuration, and security settings. Configure automated backups, maintenance windows, and monitoring options.

```bash
# Create RDS PostgreSQL instance
aws rds create-db-instance \
  --db-instance-identifier inflight-ordering-db \
  --db-instance-class db.t3.medium \
  --engine postgres \
  --engine-version 13.7 \
  --master-username dbadmin \
  --master-user-password $(aws secretsmanager get-random-password --password-length 32 --exclude-characters '"@/\' --output text --query RandomPassword) \
  --allocated-storage 100 \
  --storage-type gp2 \
  --storage-encrypted \
  --vpc-security-group-ids sg-zzzzzzzzz \
  --db-subnet-group-name inflight-ordering-db-subnet-group \
  --backup-retention-period 7 \
  --multi-az \
  --auto-minor-version-upgrade \
  --deletion-protection
```

#### Database Security Configuration

Create a dedicated security group for the RDS instance that allows access only from the ECS tasks and administrative access from specific IP ranges. Database security groups should be highly restrictive.

```bash
# Create security group for RDS
aws ec2 create-security-group --group-name rds-security-group --description "Security group for RDS PostgreSQL" --vpc-id vpc-xxxxxxxxx

# Add rule to allow access from ECS tasks
aws ec2 authorize-security-group-ingress --group-id sg-zzzzzzzzz --protocol tcp --port 5432 --source-group sg-yyyyyyyyy
```

Configure database credentials in AWS Secrets Manager with automatic rotation enabled. Secrets Manager provides secure credential storage and automatic rotation capabilities.

```bash
# Create secret for database credentials
aws secretsmanager create-secret \
  --name inflight-ordering-db-credentials \
  --description "Database credentials for inflight ordering application" \
  --secret-string '{"username":"dbadmin","password":"generated-password","engine":"postgres","host":"inflight-ordering-db.xxxxxxxxx.us-east-1.rds.amazonaws.com","port":5432,"dbname":"inflight_ordering"}'
```

### Phase 3: Container Registry and Application Deployment

#### ECR Repository Setup

Create an Amazon Elastic Container Registry (ECR) repository to store Docker images for the application. ECR provides secure, scalable container image storage with integration to ECS and other AWS services.

```bash
# Create ECR repository
aws ecr create-repository --repository-name inflight-ordering-app --image-scanning-configuration scanOnPush=true

# Get login token for Docker
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com
```

#### Docker Image Build and Push

Build the Docker image for the Java application with appropriate optimization for container deployment. Include only necessary dependencies and use multi-stage builds to minimize image size.

```dockerfile
# Dockerfile for Java application
FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/inflight-ordering-app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build and tag Docker image
docker build -t inflight-ordering-app .
docker tag inflight-ordering-app:latest 123456789012.dkr.ecr.us-east-1.amazonaws.com/inflight-ordering-app:latest

# Push image to ECR
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/inflight-ordering-app:latest
```

#### ECS Cluster and Service Configuration

Create an ECS cluster using Fargate launch type for serverless container deployment without managing underlying infrastructure. Fargate provides automatic scaling and management of container infrastructure.

```bash
# Create ECS cluster
aws ecs create-cluster --cluster-name inflight-ordering-cluster --capacity-providers FARGATE --default-capacity-provider-strategy capacityProvider=FARGATE,weight=1
```

Create task definition for the application container with appropriate resource allocation, environment variables, and security configuration.

```json
{
  "family": "inflight-ordering-task",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::123456789012:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "inflight-ordering-app",
      "image": "123456789012.dkr.ecr.us-east-1.amazonaws.com/inflight-ordering-app:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "production"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:inflight-ordering-db-credentials:password::"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/inflight-ordering",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Phase 4: Load Balancer and Auto Scaling

#### Application Load Balancer Setup

Create an Application Load Balancer to distribute traffic across multiple application instances with health checking and SSL termination capabilities.

```bash
# Create Application Load Balancer
aws elbv2 create-load-balancer \
  --name inflight-ordering-alb \
  --subnets subnet-xxxxxxxxx subnet-yyyyyyyyy \
  --security-groups sg-xxxxxxxxx \
  --scheme internet-facing \
  --type application \
  --ip-address-type ipv4
```

Create target group for ECS service with appropriate health check configuration that ensures only healthy instances receive traffic.

```bash
# Create target group
aws elbv2 create-target-group \
  --name inflight-ordering-targets \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxxxxxxxx \
  --target-type ip \
  --health-check-path /health \
  --health-check-interval-seconds 30 \
  --health-check-timeout-seconds 5 \
  --healthy-threshold-count 2 \
  --unhealthy-threshold-count 3
```

#### ECS Service with Auto Scaling

Create ECS service with auto scaling configuration that automatically adjusts capacity based on demand metrics such as CPU utilization and request count.

```bash
# Create ECS service
aws ecs create-service \
  --cluster inflight-ordering-cluster \
  --service-name inflight-ordering-service \
  --task-definition inflight-ordering-task:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxxxxxx,subnet-yyyyyyyyy],securityGroups=[sg-yyyyyyyyy],assignPublicIp=DISABLED}" \
  --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:us-east-1:123456789012:targetgroup/inflight-ordering-targets/xxxxxxxxx,containerName=inflight-ordering-app,containerPort=8080
```

Configure auto scaling policies for the ECS service based on CloudWatch metrics that reflect application load and performance characteristics.

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/inflight-ordering-cluster/inflight-ordering-service \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 2 \
  --max-capacity 10

# Create scaling policy
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/inflight-ordering-cluster/inflight-ordering-service \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json
```

### Phase 5: Frontend Deployment

#### S3 Static Website Hosting

Create S3 bucket for hosting the React frontend application with appropriate permissions and configuration for static website hosting.

```bash
# Create S3 bucket for frontend
aws s3 mb s3://inflight-ordering-frontend-bucket

# Configure bucket for static website hosting
aws s3 website s3://inflight-ordering-frontend-bucket --index-document index.html --error-document error.html

# Upload frontend files
aws s3 sync ./dist s3://inflight-ordering-frontend-bucket --delete
```

#### CloudFront Distribution

Create CloudFront distribution for global content delivery with SSL certificate and security headers configuration.

```json
{
  "CallerReference": "inflight-ordering-frontend-distribution",
  "Comment": "CloudFront distribution for inflight ordering frontend",
  "DefaultRootObject": "index.html",
  "Origins": {
    "Quantity": 1,
    "Items": [
      {
        "Id": "S3-inflight-ordering-frontend",
        "DomainName": "inflight-ordering-frontend-bucket.s3.amazonaws.com",
        "S3OriginConfig": {
          "OriginAccessIdentity": ""
        }
      }
    ]
  },
  "DefaultCacheBehavior": {
    "TargetOriginId": "S3-inflight-ordering-frontend",
    "ViewerProtocolPolicy": "redirect-to-https",
    "MinTTL": 0,
    "ForwardedValues": {
      "QueryString": false,
      "Cookies": {
        "Forward": "none"
      }
    }
  },
  "Enabled": true,
  "PriceClass": "PriceClass_All"
}
```

### Phase 6: Monitoring and Logging

#### CloudWatch Configuration

Create CloudWatch log groups for application logging with appropriate retention policies and log aggregation configuration.

```bash
# Create log group for ECS tasks
aws logs create-log-group --log-group-name /ecs/inflight-ordering --retention-in-days 30

# Create log group for ALB access logs
aws logs create-log-group --log-group-name /aws/applicationloadbalancer/inflight-ordering --retention-in-days 7
```

Configure CloudWatch dashboards for monitoring application performance, infrastructure health, and business metrics.

```json
{
  "widgets": [
    {
      "type": "metric",
      "properties": {
        "metrics": [
          ["AWS/ECS", "CPUUtilization", "ServiceName", "inflight-ordering-service"],
          ["AWS/ECS", "MemoryUtilization", "ServiceName", "inflight-ordering-service"]
        ],
        "period": 300,
        "stat": "Average",
        "region": "us-east-1",
        "title": "ECS Service Metrics"
      }
    }
  ]
}
```

#### Alerting Configuration

Create CloudWatch alarms for critical metrics with appropriate notification configuration through SNS topics.

```bash
# Create SNS topic for alerts
aws sns create-topic --name inflight-ordering-alerts

# Create CloudWatch alarm for high CPU utilization
aws cloudwatch put-metric-alarm \
  --alarm-name "High-CPU-Utilization" \
  --alarm-description "Alarm when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --alarm-actions arn:aws:sns:us-east-1:123456789012:inflight-ordering-alerts
```

## Security Hardening

### IAM Roles and Policies

Create IAM roles with least-privilege permissions for all AWS services and application components. Use managed policies where appropriate and create custom policies for specific requirements.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetSecretValue"
      ],
      "Resource": "arn:aws:secretsmanager:us-east-1:123456789012:secret:inflight-ordering-db-credentials*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:us-east-1:123456789012:log-group:/ecs/inflight-ordering*"
    }
  ]
}
```

### Encryption Configuration

Enable encryption at rest for all data storage services including RDS, S3, and EBS volumes using AWS KMS with customer-managed keys for enhanced security control.

```bash
# Create KMS key for application encryption
aws kms create-key --description "Inflight Ordering Application Encryption Key" --key-usage ENCRYPT_DECRYPT

# Create alias for the key
aws kms create-alias --alias-name alias/inflight-ordering-key --target-key-id xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

Configure SSL/TLS certificates for all public endpoints using AWS Certificate Manager with automatic renewal capabilities.

```bash
# Request SSL certificate
aws acm request-certificate \
  --domain-name inflight-ordering.example.com \
  --subject-alternative-names www.inflight-ordering.example.com \
  --validation-method DNS
```

### Network Security

Configure AWS WAF (Web Application Firewall) to protect against common web application attacks and implement rate limiting for API endpoints.

```json
{
  "Name": "inflight-ordering-waf",
  "Scope": "CLOUDFRONT",
  "DefaultAction": {
    "Allow": {}
  },
  "Rules": [
    {
      "Name": "AWSManagedRulesCommonRuleSet",
      "Priority": 1,
      "OverrideAction": {
        "None": {}
      },
      "Statement": {
        "ManagedRuleGroupStatement": {
          "VendorName": "AWS",
          "Name": "AWSManagedRulesCommonRuleSet"
        }
      },
      "VisibilityConfig": {
        "SampledRequestsEnabled": true,
        "CloudWatchMetricsEnabled": true,
        "MetricName": "CommonRuleSetMetric"
      }
    }
  ]
}
```

## Backup and Disaster Recovery

### Automated Backup Configuration

Configure automated backups for all critical data including RDS snapshots, S3 cross-region replication, and ECS task definition versioning.

```bash
# Enable automated RDS backups (configured during instance creation)
# Configure S3 cross-region replication
aws s3api put-bucket-replication --bucket inflight-ordering-frontend-bucket --replication-configuration file://replication-config.json
```

### Disaster Recovery Planning

Implement multi-region deployment capabilities for disaster recovery with automated failover procedures and data synchronization across regions.

Create disaster recovery runbooks with step-by-step procedures for various failure scenarios including region-wide outages, service failures, and data corruption events.

## Cost Optimization

### Resource Right-Sizing

Implement regular review procedures for resource utilization and cost optimization opportunities including instance type optimization, storage class optimization, and unused resource identification.

Configure AWS Cost Explorer and AWS Budgets for ongoing cost monitoring and optimization recommendations.

### Reserved Capacity Planning

Evaluate Reserved Instance and Savings Plans opportunities for predictable workloads to reduce compute costs while maintaining performance and availability requirements.

## Maintenance and Operations

### Automated Deployment Pipeline

Implement CI/CD pipeline using AWS CodePipeline, CodeBuild, and CodeDeploy for automated testing, building, and deployment of application updates.

```yaml
# CodeBuild buildspec.yml
version: 0.2
phases:
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - docker build -t $IMAGE_REPO_NAME:$IMAGE_TAG .
      - docker tag $IMAGE_REPO_NAME:$IMAGE_TAG $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing the Docker image...
      - docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_DEFAULT_REGION.amazonaws.com/$IMAGE_REPO_NAME:$IMAGE_TAG
```

### Operational Procedures

Establish operational procedures for routine maintenance, security updates, and capacity management including automated patching schedules and maintenance windows.

Create operational runbooks for common administrative tasks including scaling operations, backup restoration, and security incident response.

This comprehensive AWS deployment guide provides the foundation for deploying and operating the Inflight Ordering Application in a production AWS environment with enterprise-grade security, scalability, and reliability characteristics.

