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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {
    Store store=null;
    ScreenShotUtils screenShotUtils;
    WebDriver driver;


    public EmailUtils(WebDriver driver){
        this.driver = driver;
        screenShotUtils = new ScreenShotUtils(driver);
    }


    public String getOTPFromEmail(Map<String,String> mailUserDetails) throws InterruptedException {
        try {
            String emailProvider = mailUserDetails.get("emailProvider");
            String username = mailUserDetails.get("username");
            String password = mailUserDetails.get("password");
            String subject = "Your OTP for logging in Naukri account";

            // Wait for email to arrive
            Thread.sleep(10000);
            
            // Retrieve email body
            String emailBody = getEmailBody(emailProvider, username, password, subject);
            if (emailBody == null) {
                throw new IllegalStateException("No matching email found with subject: " + subject);
            }

            // Parse HTML and extract text
            Document doc = Jsoup.parse(emailBody);
            emailBody = doc.body().text();
            
            // Extract and return OTP
            return extractOtp(emailBody);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            screenShotUtils.takeScreenshot();
            throw new RuntimeException("Unable to retrieve OTP from email: " + e.getMessage(), e);
        }
    }

    public String getEmailBody(String emailProvider, String userName, String password, String subjectKeyword) throws Exception {
        String emailBody = null;
        Folder folderInbox = null;
        
        try {
            // Establish connection to email server
            connectEmailServer(emailProvider, userName, password);

            // Open INBOX folder
            folderInbox = openInboxFolder();

            // Email search parameters
            int pollingTime = 5;
            int pollingMaxTime = 100;
            int emailMaxTime = 300;
            int pollingIterations = pollingMaxTime / pollingTime;

            // Get current time for filtering recent emails
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.parse(dateFormat.format(Calendar.getInstance().getTime()), formatter);

            // Poll for email with matching subject
            for (int iteration = 1; iteration < pollingIterations; iteration++) {
                Message[] messages = getMessagesFromFolder(emailProvider, folderInbox);
                emailBody = searchForEmailWithSubject(messages, subjectKeyword, currentTime, emailMaxTime, dateFormat, formatter);
                
                if (emailBody != null) {
                    break;
                }
                Thread.sleep(pollingTime * 1000);
            }

            return emailBody;

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new Exception("Email retrieval interrupted", ex);
        } catch (MessagingException ex) {
            throw new Exception("Failed to retrieve email: " + ex.getMessage(), ex);
        } finally {
            closeResources(folderInbox);
        }
    }

    /**
     * Opens INBOX folder with proper error handling
     */
    private Folder openInboxFolder() throws MessagingException {
        if (store == null || !store.isConnected()) {
            throw new MessagingException("Email store is not connected");
        }

        Folder folderInbox = store.getFolder("INBOX");
        if (!folderInbox.exists()) {
            throw new MessagingException("INBOX folder does not exist");
        }

        folderInbox.open(Folder.READ_ONLY);
        return folderInbox;
    }

    /**
     * Closes resources properly
     */
    private void closeResources(Folder folder) {
        if (folder != null) {
            try {
                if (folder.isOpen()) {
                    folder.close(false);
                }
            } catch (MessagingException ex) {
                // Log but don't throw - we're in finally block
            }
        }

        if (store != null) {
            try {
                if (store.isConnected()) {
                    store.close();
                }
            } catch (MessagingException ex) {
                // Log but don't throw - we're in finally block
            }
        }
    }

    private Message[] getMessagesFromFolder(String emailProvider, Folder folderInbox) throws MessagingException {
        if (emailProvider.equalsIgnoreCase("outlook")) {
            Flags seen = new Flags(Flags.Flag.SEEN);
            Flags recent = new Flags(Flags.Flag.RECENT);
            SearchTerm searchTerm = new OrTerm(
                new FlagTerm(seen, true),
                new FlagTerm(recent, true)
            );
            return folderInbox.search(searchTerm);
        } else {
            return folderInbox.getMessages();
        }
    }

    private String searchForEmailWithSubject(Message[] messages, String subjectKeyword, LocalDateTime currentTime, 
                                            int emailMaxTime, DateFormat dateFormat, DateTimeFormatter formatter) throws MessagingException, IOException {
        int messageCount = Math.min(messages.length - 1, 9);
        
        for (int i = messages.length - 1; i >= messages.length - 10 && i >= 0; i--) {
            Message message = messages[i];
            LocalDateTime messageTime = LocalDateTime.parse(dateFormat.format(message.getReceivedDate()), formatter);
            long timeDiffSeconds = java.time.Duration.between(currentTime, messageTime).getSeconds();

            if (timeDiffSeconds < emailMaxTime) {
                if (message.getSubject() != null && message.getSubject().equalsIgnoreCase(subjectKeyword)) {
                    return getText(message);
                }
            } else {
                break;
            }
        }
        
        return null;
    }

    /**
     * Connects to email server with enhanced error handling and validation
     * Uses improved Java practices for better robustness
     */
    public void connectEmailServer(String emailProvider, String userName, String password) throws MessagingException {
        // Validate input parameters
        validateConnectionParameters(emailProvider, userName, password);

        try {
            // Get configured properties for the provider
            Properties properties = buildEmailProperties(emailProvider);

            // Create session with debug mode disabled for production
            Session session = Session.getInstance(properties);
            session.setDebug(false);

            // Attempt to get store instance
            Store tempStore = getStoreInstance(session, emailProvider);

            // Get appropriate host and establish connection
            String host = getHostForProvider(emailProvider);
            tempStore.connect(host, userName, password);

            // Verify connection is successful before assigning to instance variable
            if (!tempStore.isConnected()) {
                throw new MessagingException("Failed to establish connection - store not connected");
            }

            this.store = tempStore;

        } catch (IllegalArgumentException ex) {
            throw new MessagingException("Invalid configuration: " + ex.getMessage(), ex);
        } catch (AuthenticationFailedException ex) {
            throw new MessagingException("Authentication failed - check username and password", ex);
        } catch (NoSuchProviderException ex) {
            throw new MessagingException("Email provider protocol not available: " + ex.getMessage(), ex);
        } catch (MessagingException ex) {
            throw new MessagingException("Failed to connect to email server: " + ex.getMessage(), ex);
        }
    }

    /**
     * Validates input parameters for email connection
     */
    private void validateConnectionParameters(String emailProvider, String userName, String password) throws MessagingException {
        if (emailProvider == null || emailProvider.trim().isEmpty()) {
            throw new MessagingException("Email provider cannot be null or empty");
        }
        if (userName == null || userName.trim().isEmpty()) {
            throw new MessagingException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new MessagingException("Password cannot be null or empty");
        }
    }

    /**
     * Builds email properties with enhanced configuration for reliability
     */
    private Properties buildEmailProperties(String emailProvider) {
        Properties properties = new Properties();
        String provider = emailProvider.toUpperCase();

        switch (provider) {
            case "GMAIL":
                configureGmailProperties(properties);
                break;
            case "OUTLOOK":
                configureOutlookProperties(properties);
                break;
            default:
                throw new IllegalArgumentException("Unsupported email provider: " + emailProvider);
        }

        // Add common properties for all providers
        properties.put("mail.imaps.connectiontimeout", "10000");  // 10 seconds
        properties.put("mail.imaps.timeout", "30000");            // 30 seconds
        properties.put("mail.imaps.writetimeout", "10000");       // 10 seconds
        properties.put("mail.imaps.connectionpooltimeout", "10000");

        return properties;
    }

    /**
     * Configures Gmail-specific IMAP properties
     */
    private void configureGmailProperties(Properties properties) {
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        properties.put("mail.imaps.starttls.enable", "true");
        properties.put("mail.imaps.auth.ntlm.disable", "true");
        properties.put("mail.imaps.auth.login.disable", "false");
    }

    /**
     * Configures Outlook-specific IMAP properties
     */
    private void configureOutlookProperties(Properties properties) {
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "outlook.office365.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        properties.put("mail.imaps.starttls.enable", "true");
    }

    /**
     * Gets the appropriate store instance with error handling
     */
    private Store getStoreInstance(Session session, String emailProvider) throws MessagingException {
        try {
            return session.getStore("imaps");
        } catch (NoSuchProviderException ex) {
            throw new MessagingException("IMAPS provider not available. Please ensure javax.mail library is properly configured", ex);
        }
    }

    private String getHostForProvider(String emailProvider) {
        if (emailProvider.equalsIgnoreCase("gmail")) {
            return "imap.gmail.com";
        } else if (emailProvider.equalsIgnoreCase("outlook")) {
            return "outlook.office365.com";
        }
        throw new IllegalArgumentException("Unsupported email provider: " + emailProvider);
    }

    public void closeEmailServer() throws MessagingException {
        if (store != null && store.isConnected()) {
            store.close();
        }
    }

    /**
     * Extracts text content from email parts (handles both plain text and HTML)
     */
    private String getText(Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/*")) {
            return (String) part.getContent();
        }

        if (part.isMimeType("multipart/alternative")) {
            return getTextFromMultipartAlternative((Multipart) part.getContent());
        }

        if (part.isMimeType("multipart/*")) {
            return getTextFromMultipart((Multipart) part.getContent());
        }

        return null;
    }

    private String getTextFromMultipartAlternative(Multipart multipart) throws MessagingException, IOException {
        String plainText = null;

        for (int i = 0; i < multipart.getCount(); i++) {
            Part bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                if (plainText == null) {
                    plainText = getText(bodyPart);
                }
            } else if (bodyPart.isMimeType("text/html")) {
                String htmlText = getText(bodyPart);
                if (htmlText != null) {
                    return htmlText;
                }
            } else {
                String text = getText(bodyPart);
                if (text != null) {
                    return text;
                }
            }
        }

        return plainText;
    }

    private String getTextFromMultipart(Multipart multipart) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            String text = getText(multipart.getBodyPart(i));
            if (text != null) {
                return text;
            }
        }
        return null;
    }

    /**
     * Extracts a 6-digit OTP from the email body
     */
    private String extractOtp(String emailBody) {
        if (emailBody == null || emailBody.trim().isEmpty()) {
            throw new IllegalStateException("Email body is empty; cannot extract OTP.");
        }

        // Normalize whitespace to handle HTML parsed content
        String normalizedBody = emailBody.replaceAll("\\s+", " ");

        // Pattern to match 6-digit OTP
        Pattern otpPattern = Pattern.compile("\\b(\\d{6})\\b");
        Matcher matcher = otpPattern.matcher(normalizedBody);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalStateException("No 6-digit OTP found in the email body. Body content: " + normalizedBody);
    }
}
