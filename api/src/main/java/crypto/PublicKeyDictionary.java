package crypto;

import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Any program will only ever have one instance of this class.
 * It will keep track of Public Keys for the given keys (in this case probably IP addresses).
 */
public class PublicKeyDictionary<K> extends ConcurrentHashMap<K, PublicKey> {

}
