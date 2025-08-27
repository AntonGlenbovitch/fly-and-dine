## AWS Components for Cost Estimation

Based on the `aws-deployment-guide.md`, the following AWS services will be considered for cost estimation:

1.  **Amazon ECS (Fargate)**: For running the Java backend application.
    *   **Configuration**: Fargate launch type, `cpu`: 512 (0.5 vCPU), `memory`: 1024 (1 GB).
    *   **Assumptions**: Need to estimate average number of tasks running.

2.  **Amazon RDS (PostgreSQL)**: For the application database.
    *   **Configuration**: `db.t3.medium` instance, 100 GB GP2 storage, Multi-AZ deployment.
    *   **Assumptions**: Continuous operation.

3.  **Amazon S3**: For static web assets (frontend), application artifacts, and backups.
    *   **Configuration**: Standard storage class.
    *   **Assumptions**: Need to estimate total storage size and data transfer.

4.  **Amazon CloudFront**: For content delivery of the web application.
    *   **Configuration**: CDN for static assets.
    *   **Assumptions**: Need to estimate data transfer out.

5.  **Application Load Balancer (ALB)**: For distributing traffic to the ECS tasks.
    *   **Configuration**: Internet-facing.
    *   **Assumptions**: Need to estimate average LCU usage.

6.  **AWS KMS**: For encryption key management.
    *   **Assumptions**: Minimal usage, likely low cost.

7.  **AWS Secrets Manager**: For storing database credentials.
    *   **Assumptions**: Minimal usage, likely low cost.

8.  **Amazon CloudWatch**: For monitoring and logging.
    *   **Assumptions**: Standard logging and metrics, likely low cost.

9.  **AWS WAF**: For web application firewall.
    *   **Assumptions**: Standard rules, likely low cost.

**Services with negligible cost for this estimate (assuming standard usage):**
*   AWS IAM
*   AWS CloudTrail
*   AWS Config
*   AWS X-Ray
*   AWS Systems Manager

**Next Steps:**
To provide a more accurate estimate, I need to make some assumptions about usage patterns, such as:
- Average number of concurrent users/orders.
- Data transfer volumes (in/out).
- Database read/write operations.
- Number of ECS tasks running concurrently.

Without these, I will provide a baseline estimate for minimal usage.

