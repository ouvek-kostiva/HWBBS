//if("/Help".equals(str)){
//                        out.println("--------------------------------------------------------------------------");
//                        out.println("/Help : Show all commands");
//                        out.println("/Signup : Register a new account (Only on Entry Screen)");
//                        out.println("/Login [Your Username] [Your Password] : Login with an existing account");
//                        out.println("/Lobby : Show 10 newest posts (Require login)");
//                        out.println("/Page [Page number] : Show list of posts on page [Page number] (Require login)");
//                        out.println("/NewPost : Publish new post (Require login)");
//                        out.println("/Read [Post id] : Read Post by id (Require login)");
//                        out.println("/QuitNow : Disconnect from server");
//                        out.flush();
//                    }else if("/QuitNow".equals(str)){
//                        break;
//                    }else if("/Signup".equals(str) && "-Login".equals(cookie)){
//                        cookie = "-Signup";
//                        out.print("New Username:");out.flush();
//                    }else if("/Lobby".equals(str) && ("-"+newuser).equals(logged)){
//                        out.println("-------------------------------------------------------");
//                        out.println("Page " + page + "------ Goto page '/Page [Page number]'");
//                        out.println("-------------------------------------------------------");
//                        out.println("Post #\t Title\t\t\t Content");
//                        boolean ifPosts = getPosts(page);
//                        if(ifPosts){
//                            out.println(content);out.flush();
//                        }else{
//                            out.print("No content yet. Post your own!\r\n");out.flush();
//                        }
//                    }else if("/NewPost".equals(str) && ("-"+newuser).equals(logged)){
//                        out.println("----------------------------------");
//                        out.println("To create posts, do the following:");
//                        out.println("/PostTitle [Your post's title] : Set the title for your new post");
//                        out.println("/PostContent [Your post's content]");
//                        out.flush();
//                    }
//                    if(6 < str.length()){
//                        if("Login".equals(str.substring(1, 6)) && "-Login".equals(cookie)){
//                            String[] entry = str.split(" ");
//                            if(entry.length == 3){
//                                newuser = entry[1];
//                                pass = entry[2];
//                                String salt = getSalt(newuser);
//                                String[] saltresp = salt.split(" ");
//                                if("Not".equals(saltresp[0])){
//                                    cookie = "-Login";
//                                    out.println("Sorry, Username or Password incorrect. Please enter '/Login [Your Username] [Your Password]' to try again");out.flush();
//                                }else{
//                                    char[] enteredpass = entry[2].toCharArray();
//                                    SetPassword chkpass = new SetPassword();
//                                    out.println("Logging in....................................");out.flush();
//                                    byte[] saltblob = DatatypeConverter.parseHexBinary(saltresp[0]);
//                                    byte[] hashblob = DatatypeConverter.parseHexBinary(saltresp[1]);
//                                    out.println("Checking Password, please wait....................................");out.flush();
//                                    boolean correctPass = chkpass.isExpectedPassword(enteredpass, saltblob, hashblob);
//                                    if(correctPass){
//                                        logged = ("-"+newuser);
//                                        out.println("Login successful! Welcome back " + entry[1]);
//                                        pass = "";enteredpass = null;
//                                        cookie = "-Logged";
//                                        out.println("--------------------------------------------------------------------------");
//                                        out.println("Enter '/Help' to view all commands");out.flush();
//                                    }else{
//                                        out.println("Sorry, Username or Password incorrect. Please enter '/Login [Your Username] [Your Password]' to try again");out.flush();
//                                    }
//                                }
//                            }
//                        }
//                    }//if str.length > 5
//                    if(str.length() > 10){
//                        if("PostTitle".equals(str.substring(1, 10)) && ("-"+newuser).equals(logged)){
//                            out.println("Enter '/PostContent [Your post's content]'");
//                            out.println("When you've finished entering content Enter '/PostEnd'");
//                            out.flush();
//                            cookie = "-NewPost";
//                            postTitle = str.substring(10);
//                        }
//                    }if(str.length() > 12){
//                        if("PostContent".equals(str.substring(1, 12)) && ("-"+newuser).equals(logged) && "-NewPost".equals(cookie)){
//                            postContent = postContent + str.substring(12);
//                        }else if("PostContent".equals(str.substring(1, 12)) && ("-"+newuser).equals(logged) && !"-NewPost".equals(cookie)){
//                            out.println("Create your post's title before creating your post's content.");
//                            out.println("Enter '/PostTitle [Your post's title]' to set your new post's title");
//                            out.flush();
//                        }
//                    }if(str.length() > 7){
//                        if("PostEnd".equals(str.substring(1, 8)) && "-NewPost".equals(cookie) && ("-"+newuser).equals(logged)){
//                            System.out.println("postend");
//                            cookie = "-Logged";
//                            boolean newpost = synchronizedT.insertPost(postTitle,postContent,newuser,usernum);
//                            if(newpost){
//                                out.println("Successfully added new post!");
//                                out.println("-------------------------------------");
//                                out.flush();
//                            }else{
//                                out.println("Sorry, something went wrong. We apologize for your inconvenience.");
//                                out.flush();
//                            }
//                        }
//                    }
//
//import hwbbs.SetPassword;
//import javax.xml.bind.DatatypeConverter;
//
//
//
//if("-Signup".equals(cookie) && !"/Signup".equals(str)){
//                    newuser = str;
//                    out.println("Checking username availablity...");out.flush();
//                    boolean usernameTaken = synchronizedT.checkUsername(newuser, usernum);
//                    if(usernameTaken){
//                        out.println("Sorry, username is taken. Please enter something else:");out.flush();
//                    }else{
//                        cookie = "-SetPassword";
//                        out.println(newuser + " is available!");
//                        out.println("Password will be displayed on screen, check if there's anyone behind you!");
//                        out.println();
//                        out.print("Please enter your password:");out.flush();
//                    }
//                }
//                if("-SetPassword".equals(cookie) && !newuser.equals(str)){
//                    out.println("Generating salt...");
//                    SetPassword setpass = new SetPassword();
//                    byte[] salt = setpass.getNextSalt();
//                    out.println("Hashing your password with salt...");
//                    char[] password = str.toCharArray();
//                    byte[] hash = setpass.hash(password, salt);
//                    boolean signupresp = synchronizedT.insertPass(hash, salt, usernum, newuser);
//                    out.println("Writing data into Database...");out.flush();
//                    if(signupresp){
//                        cookie = "-Login";
//                        out.print("Signup successful! You can now login by entering '/Login [Your Username] [Your Password]'");out.flush();
//                    }else{
//                        out.println("Sorry, something went wrong. We apologize for your inconvenience.");out.flush();
//                        break;
//                    }
//                }
//
//import hwbbs.SetPassword;
//


/*
    Help
    Signup
    Login
    Lobby
    Page
    NewPost /title /content
    Read
    QuitNow
*/
