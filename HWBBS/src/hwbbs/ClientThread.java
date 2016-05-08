package hwbbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Random;
import javax.xml.bind.DatatypeConverter;

class ClientThread extends Thread{
    protected Socket incoming;
    SynchronisedThread synchronizedT = new SynchronisedThread();
    
    int usernum;
    private String cookie = "-Login";
    private String logged = "-Anon";
    private String newuser = "";
    private String pass = "";
    private int page = 1;
    private int post;
    private String content = "";
    private String postTitle = "";
    private String postContent = "";
    private String postLag = "";
    private Random random;
    short[] abgame = new short[4];

    public ClientThread(Socket incoming) {
        this.incoming = incoming;
    }
    
    public void run(){
        usernum = synchronizedT.increment();
        
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
            out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            out.println("~---------------------------------------------------------------------~");
            out.println("~ Welcome to Ouvek's BBS");
            out.println("~ Enter '/Signup' to sign up");
            out.println("~ There are currently " + synchronizedT.value() + " users online");
            out.println("~ Enter '/Login' to login");
            out.println("~ Enter '/Help' to view all commands");
            out.println("~ Enter '/QuitNow' to disconnect anytime");
            out.println("~---------------------------------------------------------------------~");
            out.flush();
            
            String str;
            
            connection: while(true){
                str = in.readLine();
                str = str.trim();
                
                if(str == null) {
                    break;
                }else if("/".equals(str.substring(0,1))){
                    switch(str.substring(1)){
                        case "Help":
                            out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                            out.println("~---------------------------------------------------------------------~");
                            out.println("/Help : Show all commands");
                            out.println("/Signup : Register a new account (Only allowed on Entry)");
                            out.println("/Login : Login with an existing account");
                            out.println("~-----------------------------------------~");
                            out.println("/Lobby : Show 10 newest posts");
                            out.println("/Page : Show different number of posts by tens");
                            out.println("/Read : Read Post by id");
                            out.println("/Game : Play Game");
                            out.println("~-----------------------------------------~");
                            out.println("/NewPost : Publish new post (Login Required)");
                            out.println("/QuitNow : Disconnect from server");
                            out.println("~---------------------------------------------------------------------~");
                            out.flush();
                            break;
                        case "QuitNow":
                            out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                            out.println("Goodbye!");out.println();out.flush();
                            break connection;
                        case "Lobby":
                            out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                            out.println("~---------------------------------------------------------------------~");
                            out.println("Currently displaying " + page + " Page(s) | use '/Page' command to change page");
                            out.println("Post id | Title ------ | Content -------------------------------------~");
                            boolean ifPosts = getPosts(page);
                            if(ifPosts){
                                out.println();out.println(content);
                                out.println("~---------------------------------------------------------------------~");
                                out.flush();
                            }else{
                                out.println();out.println("No content yet. Post your own!");
                                out.println("~---------------------------------------------------------------------~");
                                out.flush();
                            }
                            out.flush();
                            break;
                        case "Page":
                            cookie = "-Page";
                            out.print("Page:");
                            out.flush();
                            break;
                        case "Read":
                            cookie = "-Read";
                            out.print("Read Post by id: ");
                            out.flush();
                            break;
                        case "Signup":
                            if("-Login".equals(cookie)){
                                cookie = "-Signup";
                                out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                                out.print("New Username:");out.flush();
                                break;
                            }
                        case "Login":
                            if("-Login".equals(cookie)){
                                cookie = "-Logging";
                                out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                                out.print("Username:");out.flush();
                                break;
                            }
                        case "Game":
                            cookie = "-Game";
                            out.print("\n\n\n\n\n\n\n\n\n\n\n");
                            out.println("/1A1B : Number guessing game");
                            out.print("Choose game:");
                            out.flush();
                            break;
                        case "1A1B":
                            cookie = "g1A1B";
                            out.println("Loading game 1A1B ...");out.flush();
                            content = str;
                            break;
                        case "NewPost":
                            if("-Anon".equals(logged)){
                                out.println("You must login first to post!");
                                out.flush();
                            }
                            break;
                        default:
                            out.println();out.flush();
                            break;
                    }//Generic
                }if(!"-Login".equals(cookie)){
                    switch(cookie){
                        case "-Signup":
                            if(!"/Signup".equals(str)){
                                newuser = str;
                                out.println("Checking username availablity...");out.flush();
                                boolean usernameTaken = synchronizedT.checkUsername(newuser, usernum);
                                if(usernameTaken){
                                    out.println("Sorry, username is taken. Please enter something else:");out.flush();
                                }else{
                                    cookie = "-SetPassword";
                                    out.println(newuser + " is available!");
                                    out.println("Password will be displayed on screen, check if there's anyone behind you!");
                                    out.println();
                                    out.print("Please enter your password:");
                                    out.flush();
                                }
                            }
                            break;
                        case "-SetPassword":
                            if(!newuser.equals(str)){
                                char[] password = str.toCharArray();
                                out.println("Generating salt...");out.flush();
                                SetPassword setpass = new SetPassword();
                                byte[] salt = setpass.getNextSalt();
                                out.println("Hashing your password with salt...");out.flush();
                                byte[] hash = setpass.hash(password, salt);
                                out.println("Writing data into Database...");out.flush();
                                boolean signupresp = synchronizedT.insertPass(hash, salt, usernum, newuser);
                                if(signupresp){
                                    cookie = "-Login";
                                    out.println("Signup successful! You can now login by entering '/Login]'");
                                    out.flush();
                                    newuser = "";
                                    break;
                                }else{
                                    out.println("Sorry, something went wrong. We apologize for your inconvenience.");
                                    out.flush();
                                    newuser = "";
                                    break;
                                }
                            }
                            break;
                        case "-Logging":
                            if(!"/Login".equals(str)){
                                cookie = "-LogPass";
                                newuser = str;
                                out.print("Password:");
                                out.flush();
                                break;
                            }
                            break;
                        case "-LogPass":
                            if(!newuser.equals(str)){
                                pass = str;
                                out.println("Logging in............");out.flush();
                                String salt = getSalt(newuser);
                                String[] saltresp = salt.split(" ");
                                if("Not".equals(saltresp[0])){
                                    cookie = "-Login";
                                    out.println("Sorry, Username or Password incorrect. Please enter '/Login [Your Username] [Your Password]' to try again");
                                    out.flush();
                                }else{
                                    char[] enteredpass = pass.toCharArray();
                                    SetPassword chkpass = new SetPassword();
                                    byte[] saltblob = DatatypeConverter.parseHexBinary(saltresp[0]);
                                    byte[] hashblob = DatatypeConverter.parseHexBinary(saltresp[1]);
                                    boolean correctPass = chkpass.isExpectedPassword(enteredpass, saltblob, hashblob);
                                    if(correctPass){
                                        logged = ("-"+newuser);
                                        out.println("Login successful! Welcome back " + newuser);
                                        pass = "";enteredpass = null;
                                        out.println("~---------------------------------------------------------------------~");
                                        cookie = "-Logged";
                                        out.println("Enter '/Help' to view all commands");
                                        out.flush();
                                    }else{
                                        cookie = "-Login";
                                        out.println("Sorry, Username or Password incorrect. Please enter '/Login [Your Username] [Your Password]' to try again");
                                        out.flush();
                                    }
                                }
                                break;
                            }
                            break;
                        case "-PostTitle":
                            if(!"/PostTitle".equals(str) && !"/PostContent".equals(str)){
                                postTitle = str;
                                break;
                            }
                            break;
                        case "-PostContent":
                            if(!"/PostContent".equals(str) && !postLag.equals(str) && !"/PostEnd".equals(str)){
                                postLag = str;
                                postContent = postContent + postLag;
                                break;
                            }
                            break;
                        case "-Page":
                            if(!"/Page".equals(str) && !"/".equals(str.substring(0,1))){
                                page = Integer.parseInt(str);
                                out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                                out.println("~---------------------------------------------------------------------~");
                                out.println("Currently on Page " + page + " | use '/Page' command to change page");
                                out.println("Post id | Title ------ | Content -------------------------------------~");
                                boolean ifPosts = getPosts(page);
                                if(ifPosts){
                                    out.println();out.println(content);
                                    out.println("~---------------------------------------------------------------------~");
                                    out.flush();
                                }else{
                                    out.println();out.println("No content yet. Post your own!");
                                    out.println("~---------------------------------------------------------------------~");
                                    out.flush();
                                }
                                out.flush();
                                cookie = "";
                                break;
                            }
                            break;
                        case "-Read":
                            if(!"/Read".equals(str) && !"/".equals(str.substring(0,1))){
                                post = Integer.parseInt(str);
                                out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                                out.println("~---------------------------------------------------------------------~");
                                boolean ifExist = readPosts(post);
                                if(ifExist){
                                    out.print(content);
                                    out.println("~---------------------------------------------------------------------~");
                                    out.flush();
                                }else{
                                    out.println("~---------------------------------------------------------------------~");
                                    out.println("Post does not exist");
                                    out.flush();
                                }
                            }
                            break;
                        case "g1A1B":
                            out.print("\n\n\n\n\n\n\n\n\n\n");
                            out.println("Computer will set a 4 digit random number at start");
                            out.println("Digits are different to each other");
                            out.println("A marks the number of digits in the same place and same value");
                            out.println("B marks the number of digits in with the same value");
                            out.print("Answer:");
                            out.flush();
                            if("/1A1B".equals(content)){
                                abgame = random1A1B();
                                System.out.println("1A1B " + abgame[0] + abgame[1] + abgame[2] + abgame[3]);
                                content = "";
                            }
                            if(!"/1A1B".equals(str) && !content.equals(str)){
                                content = str;
                                int A = 0;
                                int B = 0;
                                for(int i = 0; i < abgame.length; i++){
                                    for (int j = 0; j < abgame.length; j++) {
                                        if(abgame[i] == Short.parseShort(content.substring(j, j+1))) A++;
                                    }
                                    if(abgame[i] == Short.parseShort(content.substring(i, i+1))){
                                        A--;B++;
                                    }
                                }
                                if(B == 4){
                                    content = "/1A1B";
                                    String ans = "";
                                    for(int i = 0; i < abgame.length; i++){
                                        ans = ans + abgame[i];
                                    }
                                    out.println("Congratulations! The answer is " + ans);
                                    out.println();
                                    out.flush();
                                    cookie = "";
                                }else{
                                    out.println(content + " : " + A + "A" + B + "B");
                                    out.print("Answer:");
                                    out.flush();
                                }
                                content = "";
                            }
                            out.flush();
                            break;
                        default:
                            break;
                    }
                }if(("-"+newuser).equals(logged)){
                    switch(str){
                        case "/NewPost":
                            out.println("~---------------------------------------------------------------------~");
                            out.println("To create posts, do the following:");
                            out.println("/PostTitle : Set the title for your new post");
                            out.flush();
                            break;
                        case "/PostTitle":
                            cookie = "-PostTitle";
                            out.println("/PostContent : Enter content for your post");
                            out.print("PostTitle:");
                            out.flush();
                            break;
                        case "/PostContent":
                            cookie = "-PostContent";
                            out.println("/PostEnd : Publish your post");
                            out.print("PostContent:");
                            out.flush();
                            break;
                        case "/PostEnd":
                            cookie = "-Logged";
                            out.println("Publishing post........");
                            out.flush();
                            boolean newpost = synchronizedT.insertPost(postTitle,postContent,newuser,usernum);
                            if(newpost){
                                out.println("Successfully added new post!");
                                out.println("~---------------------------------------------------------------------~");
                                out.flush();
                            }else{
                                out.println("Sorry, something went wrong. We apologize for your inconvenience.");
                                out.println("~---------------------------------------------------------------------~");
                                out.flush();
                            }
                            break;
                        default:
                            break;
                    }
                }
                
            }
            incoming.close();
        }catch(IOException e){
            System.err.println(usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
        }//catch
        synchronizedT.decrement();
    }//run()

    private String getSalt(String newuser) {
        try {
            Connection c = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println(usernum + " Opened database successfully: getsalt");
            PreparedStatement getsalt = c.prepareStatement("SELECT salt,passhash FROM users WHERE username LIKE ?");
            getsalt.setString(1, newuser);
            System.out.println(usernum + " Finished Query: getsalt");
            ResultSet rs = getsalt.executeQuery();
            if(rs.next()){
                System.out.println(usernum + " Has data: getsalt");
                String salt = rs.getString("salt");
                String hash = rs.getString("passhash");
                rs.close();
                c.close();
                return (salt+" "+hash);
            }else{
                System.out.println(usernum + " No data: getsalt");
                rs.close();
                c.close();
                return "Not Found";
            }
        } catch (Exception e) {
            System.err.println(usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
            return "Not Found";
        }
        
    }

    private boolean getPosts(int pg) {
        try{
            Connection c = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println(usernum + " Opened database successfully: getPosts");
            Statement stmt = null;
            stmt = c.createStatement();
            ResultSet haspost = stmt.executeQuery("SELECT EXISTS(SELECT * FROM posts)");
            int postexist = haspost.getInt("EXISTS(SELECT * FROM posts)");
            System.out.println("postexist : " + postexist);
            PreparedStatement getpost = c.prepareStatement("SELECT * FROM posts ORDER BY post_id DESC LIMIT ?");
            pg = pg*10;
            getpost.setInt(1, pg);
            ResultSet rs = getpost.executeQuery();
            ResultSetMetaData rsmeta = rs.getMetaData();
            int cols = rsmeta.getColumnCount();
            if(postexist == 1){
                content = "";
                while(rs.next()){
                    if(rs.getString("title").length() <= 20){
                        if(rs.getString("content").length() <= 40){
                            content = content + rs.getInt("post_id") + "       " + rs.getString("title") + "\t\t" + rs.getString("content") +"\r\n";
                        }else{
                            content = content + rs.getInt("post_id") + "       " + rs.getString("title") + "\t\t" + rs.getString("content").substring(0, 35) +"\r\n";
                        }
                        
                    }else{
                        if(rs.getString("content").length() <= 40){
                            content = content + rs.getInt("post_id") + "       " + rs.getString("title").substring(0, 21) + "\t\t" + rs.getString("content") +"\r\n";
                        }else{
                            content = content + rs.getInt("post_id") + "       " + rs.getString("title").substring(0, 21) + "\t\t" + rs.getString("content").substring(0, 35) +"\r\n";
                        }
                        
                    }
                    
                }
                System.out.println("true");
                return true;
            }else{
                System.out.println("false");
                return false;
            }
        }catch(Exception e){
            System.err.println(usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }

    private short[] random1A1B() {
        random = new Random();
        abgame[0] = Short.parseShort((random.nextInt((9-0)+1)+0)+"");
        while(abgame[0] == abgame[1]){
           abgame[1] = Short.parseShort((random.nextInt((9-0)+1)+0)+"");
        }
        while(abgame[0] == abgame[2] || abgame[1] == abgame[2]){
           abgame[2] = Short.parseShort((random.nextInt((9-0)+1)+0)+"");
        }
        while(abgame[0] == abgame[3] || abgame[1] == abgame[3] || abgame[2] == abgame[3]){
           abgame[3] = Short.parseShort((random.nextInt((9-0)+1)+0)+"");
        }
        
        return abgame;
    }

    private boolean readPosts(int post) {
        try{
            Connection c = null;
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:hwbbs.db");
            c.setAutoCommit(false);
            System.out.println(usernum + " Opened database successfully: readPosts");
            Statement stmt = null;
            stmt = c.createStatement();
            PreparedStatement readpost = c.prepareStatement("SELECT EXISTS(SELECT * FROM posts WHERE post_id = ?)");
            readpost.setInt(1, post);
            ResultSet rs = readpost.executeQuery();
            int postexist = rs.getInt("EXISTS(SELECT * FROM posts WHERE post_id = ?)");
            if(postexist == 1){
                System.out.println("Read Post Exist");
                readpost = c.prepareStatement("SELECT * FROM posts WHERE post_id = ?");
                readpost.setInt(1, post);
                ResultSet po = readpost.executeQuery();
                int userid = po.getInt("userid");
                ResultSet usrrs =  stmt.executeQuery("SELECT username FROM users WHERE userid =" + userid +";");
                String username = usrrs.getString("username");
                content = "";
                content = content + "Post id : " + po.getInt("post_id") + "  Post Time : " + po.getString("posttime") + "\n";
                content = content + "Author : " + username + "\n";
                content = content + "Title : " + po.getString("title") + "\n";
                content = content + "Content : \n" + po.getString("content") + "\n";
                return true;
                
            }else{
                System.out.println("Read Post Doesn't Exist");
                return false;
            }
        }catch(Exception e){
            System.err.println(usernum + " " + e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }

}//class
