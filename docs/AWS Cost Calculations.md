## AWS Cost Calculations

### 1. Amazon ECS (Fargate) Cost

**Pricing (Linux/x86, US East - N. Virginia):**
- vCPU per hour: $0.04048
- GB per hour: $0.004445

**Assumptions:**
- Baseline: 2 tasks, 24/7 (730 hours/month)
  - Each task: 0.5 vCPU, 1 GB memory
- Peak: 4 tasks, 4 hours/day (120 hours/month)
  - Each task: 0.5 vCPU, 1 GB memory

**Calculations:**

**Baseline Cost:**
- vCPU hours/month = 2 tasks * 0.5 vCPU/task * 730 hours/month = 730 vCPU-hours
- Memory GB-hours/month = 2 tasks * 1 GB/task * 730 hours/month = 1460 GB-hours
- Baseline vCPU cost = 730 vCPU-hours * $0.04048/vCPU-hour = $29.5504
- Baseline Memory cost = 1460 GB-hours * $0.004445/GB-hour = $6.4997
- **Total Baseline Cost = $29.5504 + $6.4997 = $36.0501**

**Peak Cost (additional tasks):**
- Additional tasks for peak = 4 tasks - 2 tasks = 2 tasks
- Additional vCPU hours/month = 2 tasks * 0.5 vCPU/task * 120 hours/month = 120 vCPU-hours
- Additional Memory GB-hours/month = 2 tasks * 1 GB/task * 120 hours/month = 240 GB-hours
- Peak vCPU cost = 120 vCPU-hours * $0.04048/vCPU-hour = $4.8576
- Peak Memory cost = 240 GB-hours * $0.004445/GB-hour = $1.0668
- **Total Peak Cost = $4.8576 + $1.0668 = $5.9244**

**Total Fargate Cost = Total Baseline Cost + Total Peak Cost = $36.0501 + $5.9244 = $41.9745**

**Next: Calculate RDS Cost.**



### 2. Amazon RDS (PostgreSQL) Cost

**Pricing (US East - N. Virginia):**
- `db.t3.medium` On-Demand instance: $0.072 per hour
- GP2 storage: $0.10 per GB-month
- Multi-AZ deployment: Approximately 2x single-AZ cost

**Assumptions:**
- `db.t3.medium` running 24/7 (730 hours/month)
- 100 GB GP2 storage
- Multi-AZ enabled

**Calculations:**
- Monthly instance cost (single AZ) = $0.072/hour * 730 hours/month = $52.56
- Monthly instance cost (Multi-AZ) = $52.56 * 2 = $105.12
- Monthly storage cost = 100 GB * $0.10/GB-month = $10.00
- **Total RDS Cost = $105.12 + $10.00 = $115.12**

**Next: Calculate S3 Cost.**



### 3. Amazon S3 Cost

**Pricing (US East - N. Virginia):**
- S3 Standard Storage: $0.023 per GB-month (first 50 TB)
- Data Transfer Out to Internet: $0.09 per GB (first 10 TB)

**Assumptions:**
- Frontend Assets: 1 GB storage
- Application Artifacts/Backups: 10 GB storage
- Data Transfer Out: 50 GB/month

**Calculations:**
- Total Storage = 1 GB + 10 GB = 11 GB
- Monthly storage cost = 11 GB * $0.023/GB-month = $0.253
- Monthly data transfer out cost = 50 GB * $0.09/GB = $4.50
- **Total S3 Cost = $0.253 + $4.50 = $4.753**

**Next: Calculate CloudFront Cost.**



### 4. Amazon CloudFront Cost

**Pricing (US, Mexico, Canada):**
- Data Transfer Out to Internet: $0.085 per GB (first 10 TB)
- HTTP/HTTPS Requests: $0.0075 per 10,000 requests

**Assumptions:**
- Data Transfer Out: 100 GB/month
- Requests: 10 million HTTP/HTTPS requests per month

**Calculations:**
- Monthly data transfer out cost = 100 GB * $0.085/GB = $8.50
- Monthly request cost = (10,000,000 requests / 10,000 requests) * $0.0075 = 1000 * $0.0075 = $7.50
- **Total CloudFront Cost = $8.50 + $7.50 = $16.00**

**Next: Calculate ALB Cost.**



### 5. Application Load Balancer (ALB) Cost

**Pricing (US East - N. Virginia):**
- ALB-hour: $0.0252 per hour
- LCU-hour: $0.008 per LCU-hour

**Assumptions:**
- Running 24/7 (730 hours/month)
- Average 5 LCUs per month

**Calculations:**
- Monthly hourly charge = $0.0252/hour * 730 hours/month = $18.396
- Monthly LCU cost = 5 LCUs * $0.008/LCU-hour * 730 hours/month = $29.20
- **Total ALB Cost = $18.396 + $29.20 = $47.596**

**Next: Calculate KMS Cost.**



### 6. AWS KMS Cost

**Pricing (US East - N. Virginia):**
- Customer-managed key: $1.00 per month
- Requests: $0.03 per 10,000 requests

**Assumptions:**
- 1 customer-managed key
- 10,000 requests per month

**Calculations:**
- Monthly key cost = 1 key * $1.00/month = $1.00
- Monthly request cost = (10,000 requests / 10,000 requests) * $0.03 = $0.03
- **Total KMS Cost = $1.00 + $0.03 = $1.03**

**Next: Calculate Secrets Manager Cost.**



### 7. AWS Secrets Manager Cost

**Pricing (US East - N. Virginia):**
- Secrets: $0.40 per secret per month
- API Calls: $0.05 per 10,000 API calls

**Assumptions:**
- 1 secret
- 1,000 API calls per month

**Calculations:**
- Monthly secret cost = 1 secret * $0.40/secret = $0.40
- Monthly API call cost = (1,000 API calls / 10,000 API calls) * $0.05 = 0.1 * $0.05 = $0.005
- **Total Secrets Manager Cost = $0.40 + $0.005 = $0.405**

**Next: Calculate CloudWatch Cost.**



### 8. Amazon CloudWatch Cost

**Pricing (US East - N. Virginia):**
- Custom Metrics: $0.30 per custom metric per month (for the first 10,000 metrics)
- Log Ingestion: $0.50 per GB
- Log Storage: $0.03 per GB-month

**Assumptions:**
- 1,000 custom metrics
- 10 GB of log data ingested per month
- Logs stored for 1 month

**Calculations:**
- Monthly custom metrics cost = 1,000 metrics * $0.30/metric = $300.00
- Monthly log ingestion cost = 10 GB * $0.50/GB = $5.00
- Monthly log storage cost = 10 GB * $0.03/GB-month = $0.30
- **Total CloudWatch Cost = $300.00 + $5.00 + $0.30 = $305.30**

**Next: Calculate AWS WAF Cost.**



### 9. AWS WAF Cost

**Pricing (US East - N. Virginia):**
- Web ACL: $5.00 per month
- Rule: $1.00 per month per rule
- Request: $0.60 per 1 million requests

**Assumptions:**
- 1 Web ACL
- 10 rules
- 10 million requests processed per month

**Calculations:**
- Monthly Web ACL cost = 1 Web ACL * $5.00/month = $5.00
- Monthly rule cost = 10 rules * $1.00/rule = $10.00
- Monthly request cost = (10,000,000 requests / 1,000,000 requests) * $0.60 = 10 * $0.60 = $6.00
- **Total WAF Cost = $5.00 + $10.00 + $6.00 = $21.00**

**Next: Summarize all costs.**



## Total Estimated Monthly AWS Cost

Here's a summary of the estimated monthly costs for running the Inflight Ordering Application on AWS, based on the assumptions made:

1.  **Amazon ECS (Fargate)**: $41.97
2.  **Amazon RDS (PostgreSQL)**: $115.12
3.  **Amazon S3**: $4.75
4.  **Amazon CloudFront**: $16.00
5.  **Application Load Balancer (ALB)**: $47.60
6.  **AWS KMS**: $1.03
7.  **AWS Secrets Manager**: $0.41
8.  **Amazon CloudWatch**: $305.30
9.  **AWS WAF**: $21.00

**Total Estimated Monthly Cost = $41.97 + $115.12 + $4.75 + $16.00 + $47.60 + $1.03 + $0.41 + $305.30 + $21.00 = $553.18**

**Services with negligible cost for this estimate (assuming standard usage):**
*   AWS IAM
*   AWS CloudTrail
*   AWS Config
*   AWS X-Ray
*   AWS Systems Manager

**Important Considerations:**
- This estimate is based on specific assumptions about usage patterns and current AWS pricing in the US East (N. Virginia) region. Actual costs may vary.
- Data transfer costs (especially out of AWS) can significantly impact the total bill. The estimate includes data transfer for S3 and CloudFront, but other services might incur additional data transfer costs depending on usage.
- Reserved Instances or Savings Plans can significantly reduce costs for predictable workloads, especially for RDS and Fargate.
- Monitoring and logging costs (CloudWatch) can be substantial if not managed carefully. Optimizing log retention and metrics collection can help reduce these costs.
- The cost of AWS WAF can increase with more complex rules or higher request volumes.
- This estimate does not include potential costs for AWS Support plans, which are recommended for production workloads.

This estimate provides a baseline for budgeting. A more precise estimate would require detailed usage metrics from a pilot program or a more in-depth analysis of expected traffic patterns.

