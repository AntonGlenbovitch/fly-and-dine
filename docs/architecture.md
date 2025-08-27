# Inflight Ordering Application: System Architecture Design

## 1. Introduction
This document details the high-level system architecture for the inflight ordering application, focusing on its modularity, offline capabilities, synchronization mechanisms, and security considerations. The architecture is designed to support cross-platform deployment while maintaining a single, consistent domain logic.

## 2. Architectural Overview
The application will follow a layered architecture, separating concerns into distinct modules. This approach promotes maintainability, testability, and scalability. The core domain logic will be implemented in Java, ensuring reusability across different platforms. Platform-specific UI layers will consume this core logic.

```mermaid
graph TD
    A[Mobile UI (Android)] --> C{Core Domain Logic}
    B[Desktop UI (JavaFX)] --> C
    D[Web UI (React Native Web/Vaadin)] --> C
    C --> E[Local Data Store (SQLite/Realm)]
    C --> F[Synchronization Module]
    F --> G[Core Reservation System (CRS) API]
    E --> F
    F --> E
    C --> H[Security Module]
    H --> I[Device Provisioning Service]
    H --> J[Audit Trail Service]
```

## 3. Key Architectural Components

### 3.1 Core Domain Logic
This central module will encapsulate all business rules, entities, and use cases related to order management, inventory, and seat assignments. It will be platform-agnostic and written in pure Java.

### 3.2 Local Data Store
An embedded database (e.g., SQLite for Android/Desktop, Realm for cross-platform) will be used to store all application data locally. This enables offline functionality, allowing users to create and modify orders without an active internet connection. Data will be encrypted at rest to protect PII.

### 3.3 Synchronization Module
This module is responsible for managing data consistency between the local data store and the remote Core Reservation System (CRS). It will implement a robust synchronization protocol that handles: 
- **Lossless Sync:** Ensuring all changes are eventually propagated to the CRS.
- **Idempotency:** Operations can be retried without causing duplicate or incorrect data.
- **Conflict Resolution:** Mechanisms to detect and resolve discrepancies between local and remote data, potentially involving user intervention or predefined rules.

### 3.4 Security Module
This module will enforce security policies, including:
- **Device Provisioning:** Securely onboarding and authenticating crew devices.
- **Encryption:** Managing encryption keys and encrypting/decrypting sensitive data at rest.
- **Access Control:** Implementing least-privilege access for different user roles.
- **Audit Trails:** Logging all critical actions and data access for compliance and forensic analysis.

### 3.5 Platform-Specific UIs
Separate UI layers will be developed for each target platform (Android for phones/tablets, JavaFX for laptops, potentially React Native Web or Vaadin for a web-based interface). These UIs will interact with the Core Domain Logic and present information in a platform-idiomatic manner.

## 4. Data Flow and Interactions

### 4.1 Offline Operations
When offline, the UI interacts directly with the Core Domain Logic, which in turn reads from and writes to the Local Data Store. All validations (combos, substitutions) are performed locally.

### 4.2 Online Operations and Synchronization
Upon establishing an internet connection (e.g., on landing), the Synchronization Module initiates a sync process with the CRS. It pushes local changes to the CRS and pulls updates from it. After successful CRS acceptance, the Synchronization Module triggers a confirmation message to the passenger.

## 5. Technology Stack (Proposed)
- **Core Logic:** Java
- **Local Database:** SQLite (Android), H2/SQLite (Desktop), Realm (Cross-platform consideration)
- **Synchronization:** Custom implementation with RESTful API calls to CRS
- **Security:** Android Keystore, Java KeyStore, industry-standard encryption libraries
- **Mobile UI:** Android (Kotlin/Java)
- **Desktop UI:** JavaFX
- **Web UI (Optional):** React Native Web or Vaadin (for web-based crew/passenger access)

## 6. Security Considerations
- **Data Encryption:** All PII and sensitive order data will be encrypted at rest on the device using strong encryption algorithms.
- **Secure Communication:** All communication with the CRS will be over HTTPS with proper certificate validation.
- **Authentication & Authorization:** Robust authentication mechanisms for crew devices and authorization checks for all operations.
- **Regular Security Audits:** The application will undergo regular security audits and penetration testing.

## 7. Offline-First Strategy
- **Local Data Storage:** All necessary data for order creation, modification, and validation will be available locally.
- **Conflict Resolution:** A clear strategy for resolving conflicts during synchronization will be defined, potentially involving last-write-wins, merge strategies, or user prompts.
- **Background Sync:** Synchronization will ideally happen in the background without interrupting user workflow.

## 8. Cross-Platform Strategy
- **Shared Core:** The core business logic will be a single codebase, maximizing code reuse.
- **Native UIs:** UIs will be built using native frameworks for each platform to ensure optimal performance and user experience.
- **API Layer:** The Core Domain Logic will expose a well-defined API for the UI layers to interact with.


