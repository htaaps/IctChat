package in.htlabs.tapas.ictchat;

/**
 * Created by Tapas on 5/15/2015.
 */
public class Users {
    private String name;
    private String username;

        public Users(){}

        public Users(String name,String username){
            this.name=name;
            this.username=username;
        }
        public void setUser(String name){
            this.name=name;
        }
        public String getUser(){
            return this.name;
        }
        public void setUserName(String username){
            this.username=username;
        }
        public String getUserName(){
            return this.username;
        }
}