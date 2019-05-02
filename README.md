# betterThanSkype

## Application Conventions

### Network Protocol

Packets will be transferred using the UDP protocol. Encrypted packets will always have a size of either 512 or 1024
bytes, while unencrypted packets will have a different size. This makes the maximum effective size of encrypted packets
be 1002 bytes. If a packet meant to be unencrypted ends up having a size exactly equal to 512 or 1024 bytes, an extra
null byte will be added.

A setting will control whether Multicast packets should be used, or regular Unicast packets.

### Encryption

The encryption algorithm used will be RSA, with a key size of 4096 bits. Public keys will be encoded following the X.509
standard, and private keys following the PKCS#8 standard.

## Protocols

### `PUBREQ`

```
  550 bytes
 ------------
| Public Key |
 ------------
```

The following will be the response from the server.

```
  1 byte    550 bytes
 ----------------------
| PUB     | Public Key |
 ----------------------
```

Where the operation code PUB is `00010001` (17). Upon the reception of this packet, it is expected from the client to
only communicate with the server through encrypted packets. If the request or response is lost in traffic, it is
expected from the client to make a new request.

### `CREATEROOM`

The following protocol will be used when the "Create Room" button is pressed on the client side. It is therefore
initiated by the client, with the following packet:

```
  1 byte       1 byte        ≤ 32 bytes    1 byte            ≤ 32 bytes
 --------------------------------------------------------------------------
| CREATEROOM | Name Length | Room Name   | Password Length | Room Password |
 --------------------------------------------------------------------------
```

Here, the operation code `CREATEROOM` is `00011111` (31), and `REQ#` is an incrementally increasing field, keeping track
of which requests (or successful responses) get lost. It must always start with zero for the first room creation
request.

If the room was succesffuly created, the server will respond with the following success packet:

```
  1 byte    1 byte        ≤ 32 bytes
 -------------------------------------
| CRSUC   | Name Length | Room Name   |
 -------------------------------------
```

The original request name is only included for checking, and is not actually necessary. It always will equal the request
room name. The `CRSUC` operation code is `01000111` (71). If any problem occurs while creating the room, the following
packet will be returned:

```
  1 byte    1 byte       4 bytes          ≤ 900 bytes
 ------------------------------------------------------
| ERROR   | Error Code | Message Length | Message      |
 ------------------------------------------------------
```

This error packet will be the standard error packet transmitted in every situation where an error packet is necessary.
The operation code `ERROR` for the error packet is `00010011` (19), and the error message is optional.
The error code will be one of the following:
 -  `INVALIDNAME` (`10111111` (191)) → If the room name contains some invalid characer for example.
 -  `REWP`        (`11000000` (192)) → If a room with this name already exists, with a different password.
 -  `RERP`        (`11000001` (193)) → If a room with this name already exists, with the password provided. This will
                                       allow to prompt the user to just join the room instead.
 -  `OTHER`       (`11000010` (194)) → For any other error. In this case, a message with more information will be
                                       provided.

### `JOINREQ`

Once a user has created a room, or when the "Join" button is clicked, the following packet is sent:

```
  1 byte    1 byte        ≤ 32 bytes    1 byte            ≤ 512  bytes    1 byte            ≤ 32 bytes
 ------------------------------------------------------------------------------------------------------------------
| JOINREQ | Name Length | Room Name   | Password Length | Room Password | Nickname Length | Nickname     |
 ------------------------------------------------------------------------------------------------------------------
```

If the server accepts the join requests, it will add the user to the room, and provide the user with the following:

```
  1 byte    1 byte        ≤ 32 bytes    1 byte            ≤ 512 bytes     1 byte    128 bytes      4 bytes
 -----------------------------------------------------------------------------------------------------------------
| JOINSUC | Name Length | Room Name   | Password Length | Room Password | Type    | Multicast IP | Multicast Port |
 -----------------------------------------------------------------------------------------------------------------
```

Here, `JOINSUC` is `00010111` (23). The room name is included only for verification purposes, and the password should be
identical to the room password, unless it is decided later to have the server generate a random password for each room,
in which case this documentation will be updated.
The Room Type will be one of `UNICAST` (`01001001` (73)), or `MULTICAST` (`01001111` (79)). The Multicast IP and Port
fields will be included only if the room is a Multicast room. A new protocol will need to be created for Unicast rooms.