package com.framework.utils;

import javax.mail.*;
import java.util.Properties;

/**
 * Test class to validate email server connection
 */
public class EmailServerConnectionTest {
    
    /**
     * Mock test to validate connection to email server
     */
    public static void testEmailServerConnection(String emailProvider, String userName, String password) {
        System.out.println("========== Testing Email Server Connection ==========");
        System.out.println("Provider: " + emailProvider);
        System.out.println("Username: " + userName);
        
        Store store = null;
        try {
            Properties properties = getEmailProperties(emailProvider);
            validateProperties(properties);
            
            Session session = Session.getInstance(properties);
            session.setDebug(true);
            
            store = session.getStore("imaps");
            System.out.println("Store created: " + store.getClass().getName());
            
            String host = getHostForProvider(emailProvider);
            System.out.println("Connecting to: " + host);
            
            store.connect(host, userName, password);
            System.out.println("✓ Successfully connected to email server");
            
            // Test folder access
            Folder[] folders = store.getDefaultFolder().list();
            System.out.println("✓ Available folders count: " + folders.length);
            
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            System.out.println("✓ INBOX opened successfully");
            System.out.println("✓ Total messages in INBOX: " + inbox.getMessageCount());
            inbox.close(false);
            
            System.out.println("✓ Connection Test PASSED");
            
        } catch (NoSuchProviderException ex) {
            System.err.println("✗ FAILED: Email provider not found");
            System.err.println("  Error: " + ex.getMessage());
            printStackTrace(ex);
        } catch (AuthenticationFailedException ex) {
            System.err.println("✗ FAILED: Authentication failed");
            System.err.println("  Error: Invalid username or password");
            printStackTrace(ex);
        } catch (MessagingException ex) {
            System.err.println("✗ FAILED: Messaging exception");
            System.err.println("  Error: " + ex.getMessage());
            printStackTrace(ex);
        } catch (Exception ex) {
            System.err.println("✗ FAILED: Unexpected error");
            System.err.println("  Error: " + ex.getMessage());
            printStackTrace(ex);
        } finally {
            closeStore(store);
            System.out.println("=====================================================\n");
        }
    }
    
    private static Properties getEmailProperties(String emailProvider) {
        Properties properties = new Properties();
        
        switch (emailProvider.toUpperCase()) {
            case "GMAIL":
                properties.put("mail.store.protocol", "imaps");
                properties.put("mail.imaps.host", "imap.gmail.com");
                properties.put("mail.imaps.port", "993");
                properties.put("mail.imaps.ssl.enable", "true");
                properties.put("mail.imaps.starttls.enable", "true");
                properties.put("mail.imaps.auth.ntlm.disable", "true");
                break;
            case "OUTLOOK":
                properties.put("mail.store.protocol", "imaps");
                properties.put("mail.imaps.host", "outlook.office365.com");
                properties.put("mail.imaps.port", "993");
                properties.put("mail.imaps.ssl.enable", "true");
                properties.put("mail.imaps.starttls.enable", "true");
                break;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + emailProvider);
        }
        
        return properties;
    }
    
    private static void validateProperties(Properties properties) {
        String host = properties.getProperty("mail.imaps.host");
        String port = properties.getProperty("mail.imaps.port");
        
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("Email host not configured");
        }
        if (port == null || port.isEmpty()) {
            throw new IllegalStateException("Email port not configured");
        }
        
        System.out.println("✓ Properties validated - Host: " + host + ", Port: " + port);
    }
    
    private static String getHostForProvider(String emailProvider) {
        if (emailProvider.equalsIgnoreCase("gmail")) {
            return "imap.gmail.com";
        } else if (emailProvider.equalsIgnoreCase("outlook")) {
            return "outlook.office365.com";
        }
        throw new IllegalArgumentException("Unsupported email provider: " + emailProvider);
    }
    
    private static void closeStore(Store store) {
        if (store != null) {
            try {
                if (store.isConnected()) {
                    store.close();
                    System.out.println("✓ Store closed successfully");
                }
            } catch (MessagingException ex) {
                System.err.println("✗ Error closing store: " + ex.getMessage());
            }
        }
    }
    
    private static void printStackTrace(Exception ex) {
        System.err.println("Stack trace:");
        ex.printStackTrace(System.err);
    }
    
    public static void main(String[] args) {
        // Test with Gmail
        testEmailServerConnection("GMAIL", "suresh.p.mail2026@gmail.com", "Dragonball#77");
        
        // Test with Outlook
        // testEmailServerConnection("OUTLOOK", "your-email@outlook.com", "your-password");
    }
}

