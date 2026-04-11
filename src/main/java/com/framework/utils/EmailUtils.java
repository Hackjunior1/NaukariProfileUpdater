package com.framework.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

public class EmailUtils {
    Store store=null;
    ScreenShotUtils screenShotUtils;
    WebDriver driver;


    public EmailUtils(WebDriver driver){
        this.driver = driver;
        screenShotUtils = new ScreenShotUtils(driver);
    }


    public void getOTPFromEmail(Map<String,String> mailUserDetails) {
//    public void getOTPFromEmail() {

        try {
            String emailProvider = mailUserDetails.get("emailProvider");
            String username = mailUserDetails.get("username");
            String password = mailUserDetails.get("password");
//            String subject = mailUserDetails.get("subject");
//            String startBoundary = mailUserDetails.get("startBoundary");
            String subject = null;
            String startBoundary = null;
//            String endBoundary = agentHelper.getVariableValue(dataValues[5]);
//            String localVariable = null;
//            try {
//                localVariable = mailUserDetails.get("emailProvider");
//            } catch (Exception e) {
//
//            }
            Thread.sleep(10000);
            String emailBody = getEmailBody(emailProvider, username, password, subject);
            Document doc = Jsoup.parse(emailBody);
            emailBody = doc.body().text();
            String[] emailbodyValues = emailBody.split(startBoundary);
            int endBoundary = emailbodyValues[1].length();
            String finalText = emailbodyValues[1].substring(0, endBoundary);
        //    agentHelper.setLocalVariableValue(localVariable, finalText);
            System.out.println("Successfully captured the text from email body  " + finalText);

        } catch (Exception e) {
            System.out.println("Unable to captured the text from email body"+ e);
//                    captureScreenShot(driver));
            screenShotUtils.takeScreenshot();
        }
    }

    public String getEmailBody(String emailProvider,String userName,String password, String subjectKeyword) throws Exception {
        String emailBody=null;
        boolean returnStatus=false;
        try {
            connectEmailServer(emailProvider,userName,password);
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            //create a search term for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, true);
            //create a search term for all recent messages
            Flags recent = new Flags(Flags.Flag.RECENT);
            FlagTerm recentFlagTerm = new FlagTerm(recent, true);
            SearchTerm searchTerm = new OrTerm(unseenFlagTerm,recentFlagTerm);
            // performs search through the folder

            int pollingTime =5;

            int pollinMaxTime = 100;

            int emailMaxTime = 300;

            int pollingIteration = pollinMaxTime/pollingTime;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currentDateTime=dateFormat.format(cal.getTime());
            System.out.println(currentDateTime);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime dateTime1= LocalDateTime.parse(currentDateTime, formatter);

            for(int i=1;i<pollingIteration;i++) {
                int messageCount =folderInbox.getMessageCount();
                Message[] foundMessages =null;

                if(emailProvider.equalsIgnoreCase("outlook")){
                    foundMessages = folderInbox.search(searchTerm);
                    messageCount = foundMessages.length-1;
                } else{
                    foundMessages = folderInbox.getMessages();
                    messageCount = messageCount-1;
                }
                System.out.println(i+" --- foundMessages ====> "+foundMessages.length);
                int msgLength = foundMessages.length;

                for (int k=msgLength-1 ; k>=msgLength-10;k--) {

                    if (k == -1) {
                        break;
                    }

                    Message message = foundMessages[k];

                    Address[] froms = message.getFrom();
                    String messgeDate=dateFormat.format(message.getReceivedDate());
                    System.out.println(messgeDate);
                    LocalDateTime dateTime2= LocalDateTime.parse(messgeDate, formatter);

                    long diffInSeconds = java.time.Duration.between(dateTime2, dateTime1).getSeconds();
                    if(diffInSeconds<emailMaxTime){
                        String subject = message.getSubject();
                        if (subject.equalsIgnoreCase(subjectKeyword)) {
                            emailBody = getText(foundMessages[messageCount]);
                            returnStatus =true;
                            break ;
                        }
                    } else{
                        break;
                    }

                }
                try{
                    if(!returnStatus) {
                        Thread.sleep(pollingTime*1000);
                    }else {
                        break;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
        return emailBody;
    }

    public void connectEmailServer(String emailProvider,String userName,String password)  {
        boolean returnStatus=false;
        Properties properties = new Properties();
        Message[] foundMessages =null;

        // Configure protocol / host / port per provider
        switch(emailProvider.toUpperCase()){
            case "GMAIL":
                // Use IMAPS (SSL) for Gmail
                properties.put("mail.store.protocol", "imaps");
                properties.put("mail.imaps.host", "imap.gmail.com");
                properties.put("mail.imaps.port", "993");
                properties.put("mail.imaps.ssl.enable", "true");
                break;
            case "YAHOO":
                properties.put("mail.imap.host", "imap.mail.yahoo.com");
                properties.put("mail.imap.port", 993);
                // SSL setting
                properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.imap.socketFactory.fallback", "false");
                properties.setProperty("mail.imap.socketFactory.port", String.valueOf(993));

                properties.put("mail.imap.host", "imap-mail.outlook.com");
                properties.put("mail.imap.port", 993);
                // SSL setting
                properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.imap.socketFactory.fallback", "false");
                properties.setProperty("mail.imap.socketFactory.port", String.valueOf(993));
                break;

            case "HOTMAIL":
                properties.put("mail.imap.host", "imap-mail.outlook.com");
                properties.put("mail.imap.port", 993);
                properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.imap.socketFactory.fallback", "false");
                properties.setProperty("mail.imap.socketFactory.port", String.valueOf(993));

                break;

            case "OUTLOOK":

                // Office 365 / Outlook IMAP over SSL
                properties.setProperty("mail.store.protocol", "imaps");
                properties.put("mail.imaps.host", "outlook.office365.com");
                properties.put("mail.imaps.port", "993");
                properties.setProperty("mail.imaps.ssl.enable", "true");
                properties.setProperty("mail.imaps.partialfetch", "false");
                properties.put("mail.mime.base64.ignoreerrors", "true");
                properties.setProperty("mail.debug","true");

                break;

        }
        Session session = Session.getInstance(properties);
        session.setDebug(false);
//        Session session = Session.getDefaultInstance(properties);
        try {

            // connects to the message store
            String protocol = properties.getProperty("mail.store.protocol", "imap");
            store = session.getStore(protocol);
            System.out.println("Connecting to email server for user: " + userName);

            String host = null;
            if (emailProvider.equalsIgnoreCase("gmail")) {
                host = "imap.gmail.com";
            } else if (emailProvider.equalsIgnoreCase("outlook")) {
                host = "outlook.office365.com";
            }

            if (host != null) {
                store.connect(host, userName, password);
            } else {
                store.connect(userName, password);
            }

            System.out.println("Connected to Email server…");
        }
        catch (NoSuchProviderException ex) {
            System.out.println("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store.");
            ex.printStackTrace();
        }
    }

    public void closeEmailServer(){
        try {
            store.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  To get the html body and return in string format
     */
    @SuppressWarnings("unused")
    private boolean textIsHtml = true;
    private String getText(Part p) throws MessagingException, IOException {

        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            return s;
        }
        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }
}
