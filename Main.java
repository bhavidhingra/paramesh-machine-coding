import java.util.*;
import java.io.*;

class Main {

  public static String Foo(String param) {
    return param;
  }

  public static void main (String[] args) {
    NotificationService notiService = new NotificationService();

    Notification noti1 = new Notification(1L, "200" , "205" , "2003000");
    Notification noti2 = new Notification(2L, "201.5" , "205" , "2004000");
    Notification noti3 = new Notification(3L, "207.5" , "207.5" , "2005000");


    notiService.addNotification(noti1);
    notiService.addNotification(noti2);
    notiService.addNotification(noti3);


    notiService.sendNotifications(Status.OUSTANDING);

  }

}

// NotificationService -> addNotification , sendNotification
// enum Channels -> email , sms , push nofication

class NotificationService {

    List<ChannelService> allChannels;
    Map<Status, List<Notification>> statusMap;
    
    public NotificationService() {
        statusMap = new HashMap<>();
        for(Channel channel : Channel.values()) {
            allChannels.add(channel.getChannelClass().newInstance());
        }
        for(Status status : Status.values()) {
          this.statusMap.put(status , new ArrayList<>());
        }
    }

    public Boolean addNotification(Notification notification) {
      notification.setStatus(Status.OUSTANDING);
      return statusMap.get(Status.OUSTANDING).add(notification);
    }

    public List<Notification> sendNotifications(Status status) {
      List<Notification> notis = statusMap.get(status);


      for(Notification noti : notis) {
        for(ChannelService channel : allChannels) {
          Boolean isSuccess = channel.sendNotification(noti);
          if(isSuccess) {
            statusMap.get(Status.SENT).add()
            noti.setStatus(Status.SENT);
          } else {
            noti.setStatus(Status.FAILED);
          }
        }
      }




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

  EMAIL(EmailSender.class);

  private Class<? extends ChannelService> channelClass;

  Channel (Class<? extends ChannelService>  channelClass) {
    this.channelClass = channelClass;
  }

  public Class<? extends ChannelService> getChannelClass() {
    return this.getChannelClass();
  }
}


interface ChannelService {

  Boolean sendNotification(Notification notification);

}

class EmailService implements ChannelService {

  Boolean sendNotification(Notification notification) {
      System.out.println("Sending notification via email : ");
      System.out.println(notification);
  }

}

// 1. add 3 notifications (1-> success , 2 - > fail , 3 -> outstanding)
// 2. list all by status
// send outstanding one 
// list success notifications
// delete failed notifications
// list all the notifications
// add sms channel and create a new notification and send it
