package PushNotificationServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Solution {

    //public static final String API_KEY = "AIzaSyAqb-kk1JMgQhfNWhpdZZ9izhW4TbL-ABU";
      

    public static void main(String[] args) throws Exception{
        while(true){
    	String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        System.out.println("Enum = " + weekday_name);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String str = dateFormat.format(date);
        System.out.println(str);
        String time = str.split("\\s+")[1];
        System.out.println(time);
        if(weekday_name.equalsIgnoreCase("wednesday")){
        	String str1 = time.substring(0, 2);
        	int hh = Integer.parseInt(str1);
        	if(hh >=18 && hh <=21 ){
        		System.out.println("sending Notifications");
        		/*for every instance Id in the table send notification */
            	dbConnect db = new dbConnect();
                db.sendNotifications();
                System.out.println("sleeping for an hour");
        		//sleep for an hour
            	Thread.sleep(360000);
        	}else{
        		System.out.println("sleeping for an hour");
        		//sleep for an hour
            	Thread.sleep(360000);
        	}
        }else{
        	System.out.println("sleeping for a day");
        	//sleep for a day
        	Thread.sleep(8640000);
        }
        }
    }
    
}

