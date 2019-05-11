# betterThanSkype

## Application Conventions

### Network Protocol

Packets will be transferred using the UDP protocol. Packets will have a maximum size of 1025 bytes, with the first byte
indicating which encryption method was used to encode this packet.

A setting will control whether Multicast packets should be used, or regular Unicast packets.

### Encryption

RSA will be used for client to server communication, and AES will be used for client to client communication.

## Protocols

### Public key exchange

Before sending any requests to the server, clients must obtain the server's public key, as well as communicate their own
public key. If a packet is sent unencrypted to the server, the request will not be handled and an error packet will be
sent.

```
  1 byte   4 bytes             n bytes
 -----------------------------------------
| PUB    | Public Key Length | Public Key |
 -----------------------------------------
```

### Creating a room

```
  1 byte       1 byte            ≤ 32 bytes   1 byte        ≤ 32 bytes   4 bytes           ≤ 512 bytes    1 byte
 -----------------------------------------------------------------------------------------------------------------
| CREATEROOM | Nickname Length | Nickname   | Name Length | Name       | Password Length | Password      | Type   |
 -----------------------------------------------------------------------------------------------------------------
```

If Type is `MULTICAST` (which is the only mode supported for now), the following success packet will be returned by the
server. The client is automatically added to the room and need not send an additional request to join the created room.

```
  1 byte   1 byte        ≤ 32 bytes   4 bytes         ≤ 512 bytes   1 byte      n bytes   4 bytes
 -------------------------------------------------------------------------------------------------
| MRCS   | Name Length | Name       | Secret Length | Secret      | IP Length | IP      | Port    |
 -------------------------------------------------------------------------------------------------
```

### Joining a room

```
  1 byte    1 byte            ≤ 32 bytes   1 byte        ≤ 32 bytes   4 bytes           ≤ 512 bytes    1 byte
 --------------------------------------------------------------------------------------------------------------
| JOINREQ | Nickname Length | Nickname   | Name Length | Name       | Password Length | Password      | Type   |
 --------------------------------------------------------------------------------------------------------------
```

The following packet will be returned in the Multicast case. We will have to determine another design if ever a Unicast
option is implemented.

```
  1 byte    1 byte        ≤ 32 bytes   4 bytes         ≤ 512 bytes   1 byte   1 byte      n bytes   4 bytes
 -----------------------------------------------------------------------------------------------------------
| JOINSUC | Name Length | Name       | Secret Length | Secret      | Type   | IP Length | IP      | Port    |
 -----------------------------------------------------------------------------------------------------------
```
