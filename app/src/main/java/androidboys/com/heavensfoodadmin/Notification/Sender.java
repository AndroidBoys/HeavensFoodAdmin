package androidboys.com.heavensfoodadmin.Notification;

public class Sender {

    public String to;
    public int time_to_live;
    public Notification notification;

    public Sender(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public void setTime_to_live(int time_to_live) {
        this.time_to_live = time_to_live;
    }
}
