//if("Signup".equals(str.substring(1,7)) && "-Login".equals(status)){ //Signup
//                        status = "-Signup";
//                        out.print("Enter Your New Username:");
//                        out.flush();
//                    }else if ("-Signup".equals(status)){ //check username availablity
//                    System.out.println("check");
//                    newuser = str;
//                    out.println("Checking username availablity...");
//                    out.flush();
//                    boolean usernameTaken = synchronizedT.checkUsername(newuser,usernum);
//                    if(usernameTaken){
//                        out.println("Username has already been taken, enter Your New Username to try another one.");
//                        out.flush();
//                    }else{
//                        logged = newuser;
//                        status = "-newPass";
//                        out.println("Enter '/newpass [Your Password]' to set your password");
//                        out.flush();
//                    }
//                }
//
//
//else if(newuser.equals(logged) && "-newPass".equals(status)){ //setPassword for new User
//                        char[] password = str.substring(9).toCharArray();
//                        SetPassword setPass = new SetPassword();
//                        byte[] salt = setPass.getNextSalt();
//                        byte[] hash = setPass.hash(password, salt);
//                        boolean resp = synchronizedT.insertPass(hash,salt,usernum,newuser);
//                        if(resp){
//                            out.println("Thanks for registering!");
//                            out.flush();
//                            logged = "-Anon";
//                            status = "-Login";
//                            out.println("Welcome to HuangHsinYuan's BBS");
//                            out.println("Enter '/Signup' to sign up.");
//                            out.println("There are currently" + synchronizedT.value() + "users online.");
//                            out.println("Enter '/Login [YourUsername] [YourPassword]' to login");
//                            out.flush();
//                        }else{
//                            out.println("Sorry, something went wrong!");
//                            out.flush();
//                            break;
//                        }
//                    }
//
//import hwbbs.SetPassword;
//
