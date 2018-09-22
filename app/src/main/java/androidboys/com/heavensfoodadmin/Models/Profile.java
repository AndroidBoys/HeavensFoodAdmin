package androidboys.com.heavensfoodadmin.Models;

import java.io.Serializable;

public class Profile implements Serializable {

    public User user;

    public Profile() {
    }

    public Profile(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
