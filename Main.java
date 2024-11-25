import java.util.*;
import java.io.*;

class Main {

    public static String Foo(String param) {
        return param;
    }

    // add 3 notifications (1-> success , 2 - > fail , 3 -> outstanding)
//  list all by status
// send outstanding one
// list success notifications
// delete failed notifications
// list all the notifications
// add sms channel and create a new notification and send it


    public static void main (String[] args) {
        NotificationService notiService = new NotificationService();

        Notification noti1 = new Notification(1L, "200" , "205" , "2003000");
        Notification noti2 = new Notification(2L, "201.5" , "205" , "2004000");
        Notification noti3 = new Notification(3L, "207.5" , "207.5" , "2005000");


        System.out.println("\nadded 3 notifications where id=2 is hard coded to fail\n");

        notiService.addNotification(noti1);
        notiService.addNotification(noti2);
        notiService.addNotification(noti3);

        System.out.println("\ntriggering all outstanding notifications\n");
        notiService.sendNotifications(Status.OUSTANDING);

        System.out.println("\nprinting status of all the notifications\n");
        notiService.printStatus();

        System.out.println("\nchanging id 2 to 5 so that it gets sent\n");
        noti2.setId(5L);

        System.out.println("\ntriggering all the failed notifications\n");
        notiService.sendNotifications(Status.FAILED);

        System.out.println("\nprinting status of all the notifications\n");
        notiService.printStatus();
    }

}

// NotificationService -> addNotification , sendNotification
// enum Channels -> email , sms , push nofication

class NotificationService {

    List<ChannelService> allChannels;
    Map<Status, List<Notification>> statusMap;

    public NotificationService(){
        statusMap = new HashMap<>();
        allChannels = new ArrayList<>();
        for(Channel channel : Channel.values()) {
            try {
                allChannels.add(channel.getChannelClass().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(Status status : Status.values()) {
            this.statusMap.put(status , new ArrayList<>());
        }
    }

    public void printStatus() {
        for(Status status : Status.values()) {
            System.out.println("\nNotifications with Status "+ status );
            List<Notification> notis = statusMap.get(status);
            for(Notification noti : notis) {
                System.out.println(noti.toString());
            }
        }
    }

    public Boolean addNotification(Notification notification) {
        notification.setStatus(Status.OUSTANDING);
        return statusMap.get(Status.OUSTANDING).add(notification);
    }

    public List<Notification> sendNotifications(Status status) {
        List<Notification> notis = statusMap.get(status);


        for(Notification noti : notis) {
            Boolean isSuccess = false;
            for(ChannelService channel : allChannels) {
                isSuccess = channel.sendNotification(noti);
            }
            if(isSuccess) {
                noti.setStatus(Status.SENT);
                statusMap.get(Status.SENT).add(noti);
            } else {
                noti.setStatus(Status.FAILED);
                statusMap.get(Status.FAILED).add(noti);
            }
        }

        statusMap.get(status).clear();

        return notis;

    }

}

class Notification {
    Long id;
    String currentPrice;
    String intradayHighPrice;
    String marketCap;
    Status status;

    public Notification (Long id , String currentPrice , String intradayHighPrice , String marketCap ) {
        this.id = id ;
        this.currentPrice = currentPrice;
        this.intradayHighPrice = intradayHighPrice;
        this.marketCap = marketCap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Notification( id =" + id + " , currentPrice =" + currentPrice + " , intradayHighPrice = " +intradayHighPrice  +" , marketCap = " +marketCap +")";
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

enum Status {
    OUSTANDING("outstanding"),
    SENT("sent"),
    FAILED("failed");

    private String name;

    Status(String name) {
        this.name = name;
    }
}



enum Channel {

    EMAIL(EmailService.class),
    SMS(TextService.class);

    private Class<? extends ChannelService> channelClass;

    Channel (Class<? extends ChannelService>  channelClass) {
        this.channelClass = channelClass;
    }

    public Class<? extends ChannelService> getChannelClass() {
        return this.channelClass;
    }
}


interface ChannelService {

    Boolean sendNotification(Notification notification);

}

class EmailService implements ChannelService {

    public Boolean sendNotification(Notification notification) {

        if(notification.getId() == 2) {
            return Boolean.FALSE;
        }
        System.out.print("Sending notification  via email : ");
        System.out.println(notification.toString());
        return Boolean.TRUE;
    }

}

class TextService implements ChannelService {


    public Boolean sendNotification(Notification notification) {

        if(notification.getId() == 2) {
            return Boolean.FALSE;
        }

        System.out.print("Sending notification via sms : ");
        System.out.println(notification.toString());
        return Boolean.TRUE;
    }

}
/**

 added 3 notifications where id=2 is hard coded to fail


 triggering all outstanding notifications

 Sending notification  via email : Notification( id =1 , currentPrice =200 , intradayHighPrice = 205 , marketCap = 2003000)
 Sending notification via sms : Notification( id =1 , currentPrice =200 , intradayHighPrice = 205 , marketCap = 2003000)
 Sending notification  via email : Notification( id =3 , currentPrice =207.5 , intradayHighPrice = 207.5 , marketCap = 2005000)
 Sending notification via sms : Notification( id =3 , currentPrice =207.5 , intradayHighPrice = 207.5 , marketCap = 2005000)

 printing status of all the notifications


 Notifications with Status OUSTANDING

 Notifications with Status SENT
 Notification( id =1 , currentPrice =200 , intradayHighPrice = 205 , marketCap = 2003000)
 Notification( id =3 , currentPrice =207.5 , intradayHighPrice = 207.5 , marketCap = 2005000)

 Notifications with Status FAILED
 Notification( id =2 , currentPrice =201.5 , intradayHighPrice = 205 , marketCap = 2004000)

 changing id 2 to 5 so that it gets sent


 triggering all the failed notifications

 Sending notification  via email : Notification( id =5 , currentPrice =201.5 , intradayHighPrice = 205 , marketCap = 2004000)
 Sending notification via sms : Notification( id =5 , currentPrice =201.5 , intradayHighPrice = 205 , marketCap = 2004000)

 printing status of all the notifications


 Notifications with Status OUSTANDING

 Notifications with Status SENT
 Notification( id =1 , currentPrice =200 , intradayHighPrice = 205 , marketCap = 2003000)
 Notification( id =3 , currentPrice =207.5 , intradayHighPrice = 207.5 , marketCap = 2005000)
 Notification( id =5 , currentPrice =201.5 , intradayHighPrice = 205 , marketCap = 2004000)

 Notifications with Status FAILED
 **/

