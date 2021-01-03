 
import java.util.*   ;
import java.text.*   ;
import com.newsware.*;
import com.newsware.nwGateway.*;
 
public class NewsProcessor 
{
  nwGateway gw;
  private class nwLoginListener implements nwGateway.LoginListener
  {
    public void login(nwGateway.LoginEvent e)
    {
      int  retCode ;
      byte reqType = nwGlobals.NWRT_ALERT;
      String expr = "s.ir s.in s.bw s.pn s.pz s.ar s.dj s.pr";
      int maxlines =0;
      int fromTime = 0;
      int toTime = 0;
      String storyID = "";
      short specialRequest = 0;
      int reqID = 0;
      System.out.println("Starting thread");
      TimerThread timerThread = new TimerThread();
      timerThread.start();
      
      retCode =  e.getRetCode();
      if(retCode == 0)
      {
        gw.headlinesRequest(reqID,reqType,expr,maxlines,fromTime,toTime,storyID, specialRequest);
      }
      else
      {
        System.out.println("login failed w/return code: " + e.getRetCode());
        return; 
      }
    }
  }
  public void start()
  {
    gw = new nwGateway();
    nwLoginListener listener = new nwLoginListener();
    gw.addLoginListener(listener);
    String userID = "ENTER_USER_ID";
    String password = "";
    String address = "newstrack.newsware.com";
    String version = "5:5.0.3";
    gw.logon(userID, password,address, version);
    System.out.println("Login OK");

    gw.addHeadlineListener(evt -> {
      HdlObject HDL = new HdlObject();
      HDL = evt.getHdl();
      if(HDL.primaryTicker.isEmpty()){
        System.out.println("skipping no ticker");
        return;
      }
      SimpleDateFormat longDate = new SimpleDateFormat("MM/dd HH:mm:ss");
      Date date = new Date(((long)HDL.time)*1000);
      String dateFormat = longDate.format(date);
      String monthDay = dateFormat.substring(0, 5) + ' ';
      //dateFormat = monthDay + dateFormat.substring(6, 11);
      System.out.println("headline: HDL text = " + HDL.text);
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
      System.out.println(formatter.format(new Date(System.currentTimeMillis())));
      System.out.println("headline: date = " + dateFormat);
      //System.out.println("headline: vendorID = " + HDL.vendorID);
      //System.out.println("headline: storyID = " + HDL.storyID);

      //System.out.println("headline: reqType = " + HDL.reqType);
      //System.out.println("headline: indices = " + HDL.indices);
      System.out.println("headline: primaryTicker = " + HDL.primaryTicker);
      System.out.println("headline: exchange = " + HDL.exchange);
      //System.out.println("headline: usn = " + HDL.usn);
      //System.out.println("headline: hotStory = " + HDL.hotStory);
      //System.out.println("headline: extendedStory = " + HDL.extendedStory);
      //System.out.println("headline: tempStory = " + HDL.tempStory);
      //System.out.println("headline: storyPart = " + HDL.storyPart);
      //System.out.println("headline: rank = " + HDL.rank);
      //System.out.println("headline: rating = " + HDL.rating);
      String  storyID = HDL.storyID;
          String  searchExpr = "";
        byte    reqID = 0;

          gw.storyRequest(reqID, storyID, searchExpr);
    });
    gw.addStoryResponseListener(e -> {
      StoryResponseObject stResponse = e.getStoryResponse();
      System.out.println("story: reqId = " + stResponse.reqID);
      //System.out.println("story: storyId = " + stResponse.storyID);
      //System.out.println("story: retcode = " + stResponse.retCode);
      System.out.println("story: time = " + stResponse.time);
      System.out.println("story: vendor name = " + stResponse.vendorName);
      System.out.println("story: headline text = " + stResponse.headline.text);
      //System.out.println("story: length = " + stResponse.headline.textLen);
      System.out.println("story: hilite count = " + stResponse.headline.hiliteCnt);
      for(int i = 0; i < stResponse.headline.hiliteCnt; i++)
      {
        System.out.println("story: pos = " + stResponse.headline.hilite[i].pos);
        System.out.println("story: count = " + stResponse.headline.hilite[i].cnt);
      }
      System.out.println("story: story text = " + stResponse.story.text);
      //System.out.println("story: length = " + stResponse.story.textLen);
      //System.out.println("story: hilite count = " + stResponse.story.hiliteCnt);
      for(int i = 0; i < stResponse.story.hiliteCnt; i++)
      {
        System.out.println("story: pos = " + stResponse.story.hilite[i].pos);
        System.out.println("story: count = " + stResponse.story.hilite[i].cnt);
      }
      //System.out.println("story: indices = " + stResponse.indx.text);
      //System.out.println("story: length = " + stResponse.indx.textLen);
      //System.out.println("story: hilite count = " + stResponse.indx.hiliteCnt);

      for(int i = 0; i < stResponse.indx.hiliteCnt; i++)
      {
        System.out.println("story: pos = " + stResponse.indx.hilite[i].pos);
        System.out.println("story: count = " + stResponse.indx.hilite[i].cnt);
      }
    });
  
    gw.addSockExceptionListener(e -> {
      System.out.println("addSockExceptionListener: error message = " + e.getErrorMsg());
      gw.logon("userID", "","newstrack.newsware.com", "");
    });
          
    gw.addErrorMsgListener(e -> System.out.println(e.getErrorMsg()));
          
  }
 
      		
  private class TimerThread extends Thread
  {
    public void run()
    {
      System.out.println("Started hearthbeat thread");
      while(true)
      {
        gw.sendHeartbeat(); 
        try
        {
          sleep(10000);
        }
        catch(Exception e)
        {
          System.out.println("An exception occurred when sending a heartbeat");
        }
      }
    }
  }

  public static void main(String[] args)
  {
    System.out.println("its here\n");
    NewsProcessor nc = new NewsProcessor();
    System.out.println("its not here\n");
    nc.start();
  }
}