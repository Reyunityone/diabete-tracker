package application.classiGeneriche;

public class Session {
    private static Session session;
    private User user;

    private Session(){};

    public static synchronized Session getInstance(){
        if(session == null) session = new Session();
        return session;
    }

    public User getCurrentUser(){
        return user;
    }

    public void setCurrentUser(User user){
        this.user = user;
    }

    public void logout(){
        this.user = null;
    }
}
