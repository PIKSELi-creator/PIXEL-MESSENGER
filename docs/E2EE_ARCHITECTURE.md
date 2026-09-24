# PIXEL CHAT E2EE architecture

The Java application server does not decrypt messages. It authenticates users,
checks recipients, stores message metadata, and carries an opaque Base64 payload.

Flow:
client A -> encrypt locally -> SEND_MESSAGE -> Java server -> store/relay -> client B -> decrypt locally

TLS 1.3 protects the transport. E2EE protects message content from the server.

Important: X25519 is a key-agreement primitive, not a complete authenticated
messaging protocol by itself. A production messenger still needs authenticated
identities / key verification, key rotation, replay protection, device handling,
forward secrecy, recovery strategy, and an external cryptographic review.

The updater therefore does not claim "production-certified E2EE". It installs the
server-side boundary needed for E2EE and leaves private-key operations on clients.
