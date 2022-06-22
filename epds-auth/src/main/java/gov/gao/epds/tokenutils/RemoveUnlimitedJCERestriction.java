package gov.gao.epds.tokenutils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 
 Basically by default Java doesn't allow Users to create > 256 bit keys out of the box. We can resolve the by 

1) Install the unlimited strength policy files. In this case we need to install the jar's every time we update the 
Java, they must be installed in the JRE directory (which may even be read-only due to permissions).

(http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html). 


2) Do the following, but with reflection to bypass access checks:

	         * JceSecurity.isRestricted = false;
	         * JceSecurity.defaultPolicy.perms.clear();
	         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
	         

Amer : Since we are doing encryption/decryption on the server level and we will be doing vertical clustering, 
all the instances of the jboss will be on same server. 

We can communicate with NITC to set up the right permission on the JRE directory and 
install the unlimited strength policy files. 

For Now We can just use reflection to bypass the access checks. 
Come Back to this

For sure in production we will actually need to put the Unlimited JCRE policy files into

$JAVA_HOME/jre/lib/security The below solution should only be used for testing purposes

 * @author MHussaini
 *
 */
public class RemoveUnlimitedJCERestriction {

	private final static Logger logger = LoggerFactory
			.getLogger(RemoveUnlimitedJCERestriction.class);
	public static void removeCryptographyRestrictions() { if (!isRestrictedCryptography()) {
        logger.info("Cryptography restrictions removal not needed");
        return;
    }
    try {
    	
    	if (Cipher.getMaxAllowedKeyLength("AES") > 256){
			
    		return;	
    		}
        /*
         * Do the following, but with reflection to bypass access checks:
         *
         * JceSecurity.isRestricted = false;
         * JceSecurity.defaultPolicy.perms.clear();
         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
         */
        final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
        final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
        final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

        final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
        isRestrictedField.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
        isRestrictedField.set(null, false);

        final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
        defaultPolicyField.setAccessible(true);
        final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

        final Field perms = cryptoPermissions.getDeclaredField("perms");
        perms.setAccessible(true);
        ((Map<?, ?>) perms.get(defaultPolicy)).clear();

        final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        defaultPolicy.add((Permission) instance.get(null));

        logger.info("Successfully removed cryptography restrictions");
        System.out.println("Successfully removed cryptography restrictions");
        
    } catch (final Exception e) {
    	
    	e.printStackTrace();
    	
    	System.out.println("Failed to remove cryptography restrictions");
    }}

	private static boolean isRestrictedCryptography() {
	    // This simply matches the Oracle JRE, but not OpenJDK.
	    return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}
}
