# betterThanSkype

## Application Conventions

 -  Packets will be transferred using the UDP protocol. Encrypted packets
    will always have a size of 1024 bytes, while unencrypted packets will
    have a smaller size. This makes the maximum effective size of
    encrypted packets be 1002 bytes.

 -  The Encryption algorithm used will be RSA, with a key size of 4096
    bits (which will correspond to 550 bytes when transmitting a public
    key). Public keys will follow the X.509 standard, and private keys
    the PKCS#8 standard.

 -  All packets but the initial public key request will be encrypted.
    Public key requests will consist of only the requester's public key.

 -  It is assumed that there are no bad actors within a room. That is, no
    one will provide wrong information if requested.

## Protocols

### PUBREQ

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

Where the operation code PUB is `00010001` (17). Upon the reception of
this packet, it is expected from the client to only communicate with the
server through encrypted packets. If the public key packet is lost in
traffic, it is expected from the client to make a new request.
