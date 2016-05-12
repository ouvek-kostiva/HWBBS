package hwbbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

class SynchronisedThread {
    static private int online = 0;
    
    public synchronized int increment(){
        online++;
        return online;
    }
    public synchronized void decrement(){
        online--;
    }
    public synchronized int value() {
        return online;
    }
    
    public synchronized boolean checkUsername(String username, int usernum){
        try {
            Connection c = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println(usernum + " Opened database successfully:check");
            
            PreparedStatement checkusr = c.prepareStatement("SELECT * FROM users WHERE username = ?");
            checkusr.setString(1, username);
            ResultSet rs = checkusr.executeQuery();
            if(rs.next()){
                rs.close();
                c.close();
                return true;
            }else{
                rs.close();
                c.close();
                return false;
            }
        } catch (Exception e) {
            System.err.println(usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
            return true;
        }
    }
    
    public synchronized boolean insertPass(byte[] hash, byte[] salt, int usernum, String newuser){
        String hashhex = DatatypeConverter.printHexBinary(hash);
        String salthex = DatatypeConverter.printHexBinary(salt);
        
        Connection c = null;
        try{
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println(usernum + " Opened database successfully:insert");
            
            ResultSet rs = c.createStatement().executeQuery("SELECT userid FROM users ORDER BY userid DESC LIMIT 1;");
            int userid = 0;
            if(rs.next()){
                userid = rs.getInt("userid");
            }else{
                userid = userid + 1;
            }
            userid = userid + 1;
            PreparedStatement signup = c.prepareStatement("INSERT INTO users VALUES (?,?,?,?)");
            
            signup.setInt(1, userid);
            signup.setString(2, newuser);
            signup.setString(3, hashhex);
            signup.setString(4, salthex);
            signup.executeUpdate();

            c.commit();
            c.close();
            System.out.println(usernum + "Records created successfully");
            return true;
        }catch(Exception e){
            System.err.println( usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }
    
    public synchronized boolean insertPost(String postTitle, String postContent, String newuser, int usernum){
        try{
            Connection c = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully:insertPost");
            
            ResultSet rs = c.createStatement().executeQuery("SELECT post_id FROM posts ORDER BY post_id DESC LIMIT 1;");
            int post_id = 0;
            if(rs.next()){
                post_id = rs.getInt("post_id");
            }else{
                post_id = post_id + 1;
            }
            post_id = post_id + 1;
            PreparedStatement checkusr = c.prepareStatement("SELECT userid FROM users WHERE username = ?");
            checkusr.setString(1, newuser);
            ResultSet chkusrrs = checkusr.executeQuery();
            int userid = chkusrrs.getInt("userid");
            
            c.commit();
            
            PreparedStatement insertpost = c.prepareStatement("INSERT INTO posts VALUES (?,?,?,?,?);");
            
            Date date = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate= format.format(date);
            
            insertpost.setInt(1, post_id);System.out.println("post_id : "+post_id);
            insertpost.setString(2, postTitle);System.out.println("postTitle : "+postTitle);
            insertpost.setString(3, postContent);System.out.println("postContent : "+postContent);
            insertpost.setString(4, formattedDate);System.out.println("formattedDate : "+formattedDate);
            insertpost.setInt(5, userid);System.out.println("userid : "+userid);
            insertpost.executeUpdate();
            
            c.commit();
            insertpost.close();
            chkusrrs.close();
            checkusr.close();
            rs.close();
            c.close();
            return true;
        }catch(Exception e){
            System.err.println(" " + e.getClass().getName() + ": " + e.getMessage() + ":insertPost" );
            return false;
        }
    }
    
}
