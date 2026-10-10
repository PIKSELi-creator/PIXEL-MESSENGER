# PIXEL CHAT — backend MVP

Java 21, Spring Boot, PostgreSQL, Flyway, REST API, bearer-token sessions.
This master generator is designed for incremental reruns; it backs up managed
files before changing them and refuses to overwrite files edited after a run.

## Implemented by this generator
- Email/password registration; password hashes use BCrypt.
- Six-digit email verification codes with 10-minute expiry and 5 attempts.
- SMTP email delivery when configured; in development mode only, codes are logged to the server console.
- Login/logout with random opaque bearer tokens. Only SHA-256 token hashes are stored in PostgreSQL.
- Profile read/update and verified-user search.
- Contact requests, accepting requests, listing contacts and removal.
- Direct chats, group creation, text messages and paginated-by-limit message history.
- Chat-membership checks on message routes.

## Not yet production-complete
Real-time WebSocket destination authorization, attachment storage, password recovery,
abuse limits across instances, push notifications,
audio/video calls, admin/moderation workflow, E2E encryption and mobile-client
integration still need separate implementation and tests. Do not expose this
MVP publicly until it has been reviewed, configured, and tested.

## Required environment
- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- To send email: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`
- `PIXEL_CHAT_DEV_MODE=true` only for local testing; if SMTP is not configured, the verification code is printed in the console. Never use this in production.

Default database URL: `jdbc:postgresql://localhost:5432/pixelchat`.
Use `mvn -DskipTests package` to compile. The app requires a reachable PostgreSQL database at startup because Flyway migrations and JPA validate the schema.
