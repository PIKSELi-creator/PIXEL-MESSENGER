# PIXEL CHAT roadmap

## In this MVP generator
- Email/password registration, email verification, login/logout and hashed bearer sessions
- Profile read/update, verified user search, contacts and requests
- Direct/group chat creation, text message sending, history and mark-as-read receipt storage

## Next engineering phases
1. Integration tests against PostgreSQL and API contract tests.
2. Per-message delivered/read receipts and edit/delete/reaction/reply endpoints.
3. WebSocket authentication and per-chat subscription/send authorization.
4. Attachment storage with size/type validation.
5. Password recovery, resend-code rate limits and broader abuse protection.
6. Push notifications, deployment, observability and backup/restore.
7. Android client integration.

Audio/video needs WebRTC infrastructure. End-to-end encryption should use a reviewed protocol, not custom cryptography.
