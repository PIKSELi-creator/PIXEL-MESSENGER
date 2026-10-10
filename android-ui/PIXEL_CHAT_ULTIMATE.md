# PIXEL CHAT — Android integration stage

This generator adds a reusable Android API layer and Material 3 design kit while preserving the existing `MainActivity.kt` and the `server-java-10` rollback server.

## API layer

- Retrofit + Gson with bearer-token interceptor.
- Endpoints matching the current Spring Boot MVP: registration, email verification, login/logout, profile, user search, contacts, chats, messages and read receipts.
- `pixelChatErrorMessage(Throwable)` produces a user-friendly text for common network/API failures.
- Create the service with `PixelChatApiClient.create(baseUrl) { currentAccessToken }`.

## Material 3 design kit

- `PixelChatDesignTheme { ... }`
- `PixelChatNavigationBar`
- `PixelChatSearchField`
- `PixelChatAvatar`
- `PixelChatConversationRow`
- `PixelChatMessageBubble`
- `PixelChatComposer`
- `PixelChatCreateButton`

These are new reusable components; they are not injected into MainActivity automatically because its existing navigation/screen structure must be preserved and checked before integration.

## Base URL

Use the public HTTPS URL when deployed. During local development on the same Android device, the Termux server can be reached at `http://127.0.0.1:8080/`; Android's cleartext HTTP policy may require temporarily opting into cleartext for development. Do not enable HTTP for production.

## Important status

Creating the API client does not itself make the UI perform requests. Wire the service into screen state/ViewModels and provide a token store as a separate integration step. Email verification needs configured SMTP (or the server's explicitly enabled development mode). Build and test the server and Android app after applying.
