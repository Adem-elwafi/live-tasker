# Startup Issue Report (2026-04-26)

## Symptoms

Application startup failed with errors like:

- `UnsatisfiedDependencyException` for `jwtAuthenticationFilter` / `jwtService`
- `Communications link failure` from MySQL driver
- `Failed to initialize JPA EntityManagerFactory`

## Root Causes

1. Missing JWT configuration properties
- `JwtService` uses:
  - `application.security.jwt.secret-key`
  - `application.security.jwt.expiration`
- These properties were not defined, so Spring could not fully initialize auth-related beans.

2. Wrong database port in datasource URL
- Config pointed to MySQL on port `3308`.
- Actual local MySQL (`mysqld.exe`) was listening on port `3306`.
- Result: Hibernate/Hikari could not open a JDBC connection.

## Fixes Applied

1. Added required JWT properties in `src/main/resources/application.yml`:

```yml
application:
  security:
    jwt:
      secret-key: ${JWT_SECRET:live-tasker-super-secret-key-2026-please-change}
      expiration: ${JWT_EXPIRATION:86400000}
```

2. Corrected datasource port in `src/main/resources/application.yml`:

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/live_tasker_db?createDatabaseIfNotExist=true&serverTimezone=UTC
```

## Verification

- MySQL service was confirmed running.
- Port check confirmed `3306` is listening.
- Spring Boot startup then succeeded:
  - Hikari pool started
  - JPA EntityManagerFactory initialized
  - Tomcat started on port `8080`
  - `Started LiveTaskerApplication`

## Note About Maven `BUILD FAILURE`

If you still see `spring-boot:run ... Process terminated with exit code: -1` after startup logs show `Started LiveTaskerApplication`, that typically means the run process was stopped/interrupted, not that startup configuration is broken.

## Optional Cleanup

You may remove the explicit deprecated dialect setting and let Hibernate auto-detect MySQL dialect:

- Current setting: `spring.jpa.database-platform: org.hibernate.dialect.MySQL8Dialect`
