package gov.gao.epds.passwordutils.test;

import gov.gao.epds.passwordutils.PasswordStorage;
import gov.gao.epds.passwordutils.PasswordStorage.CannotPerformOperationException;
import gov.gao.epds.passwordutils.PasswordStorage.InvalidHashException;

public class Test
{

    public static void main(String[] args) {
        /*basicTests();
        truncatedHashTest();
        testHashFunctionChecking();*/
    	
    	generatePasswordHash();
    }

    
    public static void generatePasswordHash(){
    	String userString = "0546b79346cd4dde4ec54c34ed5c6a7e388abf67a1ed271e3aa7b3f3b6e6fe9341f6f2ec30c1d23f9151b8d20b093818942f3a7034a22f9deaa7025cc923ff5e";
        String goodHash = "";
        Boolean passwordVerified = false;
    	try {
            goodHash = PasswordStorage.createHash(userString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    	Integer length  = goodHash.length();
    	try {
    		passwordVerified = PasswordStorage.verifyPassword(userString, goodHash);
		} catch (CannotPerformOperationException | InvalidHashException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
    	
    	if (passwordVerified){
    		
    	}
    }
    // Make sure truncated hashes don't validate.
    public static void truncatedHashTest() {
        String userString = "password!";
        String goodHash = "";
        String badHash = "";
        int badHashLength = 0;

        try {
            goodHash = PasswordStorage.createHash(userString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        badHashLength = goodHash.length();

        do {
            badHashLength -= 1;
            badHash = goodHash.substring(0, badHashLength);

            boolean raised = false;
            try {
                PasswordStorage.verifyPassword(userString, badHash);
            } catch (PasswordStorage.InvalidHashException ex) {
                raised = true;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }

            if (!raised) {
                System.out.println("Truncated hash test: FAIL " +
                    "(At hash length of " +
                    badHashLength + ")"
                    );
                System.exit(1);
            }

        // The loop goes on until it is two characters away from the last : it
        // finds. This is because the PBKDF2 function requires a hash that's at
        // least 2 characters long.
        } while (badHash.charAt(badHashLength - 3) != ':');

        System.out.println("Truncated hash test: pass");
    }

    /**
     * Tests the basic functionality of the PasswordStorage class
     *
     * @param   args        ignored
     */
    public static void basicTests()
    {
        try
        {
            // Test password validation
            boolean failure = false;
            for(int i = 0; i < 10; i++)
            {
                String password = ""+i;
                String hash = PasswordStorage.createHash(password);
                String secondHash = PasswordStorage.createHash(password);
                if(hash.equals(secondHash)) {
                    System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
                    failure = true;
                }
                String wrongPassword = ""+(i+1);
                if(PasswordStorage.verifyPassword(wrongPassword, hash)) {
                    System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
                    failure = true;
                }
                if(!PasswordStorage.verifyPassword(password, hash)) {
                    System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
                    failure = true;
                }
            }
            if(failure) {
                System.out.println("TESTS FAILED!");
                System.exit(1);
            }
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            System.exit(1);
        }
    }

    public static void testHashFunctionChecking()
    {
        try {
            String hash = PasswordStorage.createHash("foobar");
            hash = hash.replaceFirst("sha2:", "sha512:");

            boolean raised = false;
            try {
                PasswordStorage.verifyPassword("foobar", hash);
            } catch (PasswordStorage.CannotPerformOperationException ex) {
                raised = true;
            }

            if (raised) {
                System.out.println("Algorithm swap: pass");
            } else {
                System.out.println("Algorithm swap: FAIL");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}