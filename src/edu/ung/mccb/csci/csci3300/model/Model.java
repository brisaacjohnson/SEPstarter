package edu.ung.mccb.csci.csci3300.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Model {

    // This method doesn't implements PreparedStatement so it is vulnerable for sql injection .
    public int saveUserIntoDatabaseSQLInjection(String username, String email, String password, String salt) {
        int affectedRow=0;
        String query = "insert into sepuser(username, email, password, salt) values('"+ username +"','"+ email +"','"+ password +"','"+ salt +"')";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement sqlStatement = conn.createStatement();) {
                    // get the number of return rows
            affectedRow = sqlStatement.executeUpdate("query");
        } catch (Exception e) {
            System.out.println("Status: operation failed due to " + e);
        }
        return affectedRow;
    }
    // This method  implements PreparedStatement properly.
   public int saveUserIntoDatabase(String username, String email, String password, String salt) {
       int affectedRow=0;
       String query = "insert into sepuser" + "(username, email, password, salt)"
               + "values(?,?,?,?)";

       try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement sqlStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
           sqlStatement.setString(1, username);
           sqlStatement.setString(2, email);
           sqlStatement.setString(3, password);
           sqlStatement.setString(4, salt);

           // get the number of return rows
            affectedRow = sqlStatement.executeUpdate();

       } catch (Exception e) {
           System.out.println("Status: operation failed due to " + e);

       }
       return affectedRow;

   }

//Method to compute the salted hashed password and returns the result as string
// It accepts plain text password and reads the randomly  generated salt from a file

    public String generateSaltedHashedPassword(String passwordToHash, String salt)  {
        String generatedHashedPassword = null;
        BufferedReader readSalt = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            //Plain text password: passwordToHash
            messageDigest.update(passwordToHash.getBytes(StandardCharsets.UTF_8));
                  messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            StringBuilder sBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedHashedPassword = sBuilder.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        finally {
            if(readSalt!= null) {
                try {
                    readSalt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return generatedHashedPassword;
    }
    public boolean verifyLogin(String password, String username)
    {
        boolean isRegistred=false;
        String query = "select * from sepuser where username='"+username+"'";

            try {

                Connection conn = DatabaseConfig.getConnection();
                Statement stmts = (Statement) conn.createStatement();
                ResultSet rs = stmts.executeQuery(query);
                if (!rs.next()) {
                    throw new SecurityException("username not registered");
                } else {
                    String storedHashedPassword = rs.getString("password");
                    String storedSalt = rs.getString("salt");
                    String genreatedHashedPassword = generateSaltedHashedPassword(password, storedSalt);
                    if (storedHashedPassword.equals(genreatedHashedPassword)) {
                        isRegistred = true;
                    } else {
                        System.out.println("Login failed");
                    }

                }
                } catch (Exception e) {
                System.out.println("Status: operation failed due to " + e);

            }
            return isRegistred;

        }

}
