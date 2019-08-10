package com.orderon.commons;
/*
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail {

    public boolean sendEmail(ArrayList<String> recipents, String subject, String text) {
    	if(Configurator.getIsServer())
    		return true;
    	
    	final String[] username = {"m@orderon.co.in", "o@orderon.co.in", "support@orderon.co.in"};
		final String password = "TOMTech$1234";

		Properties props = new Properties();
		
		props.put("mail.smtp.host", "smtpout.asia.secureserver.net"); //SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "25"); //SMTP Port
		Random random = new Random();

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username[random.nextInt(3)], password);
			}
		});

		try {

			MimeMessage msg = new MimeMessage(session);
			
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		    msg.addHeader("format", "flowed");
		    msg.addHeader("Content-Transfer-Encoding", "8bit");
			
		    msg.setFrom(new InternetAddress(username[2], "NoReply-OrderOn"));

		    msg.setReplyTo(InternetAddress.parse("support@orderon.co.in", false));

		    msg.setSubject(subject, "UTF-8");
		
		    msg.setContent(text, "text/html; charset=utf-8");
		    msg.setSentDate(new Date());
		  
		    for (String recipent : recipents) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipent, false));
				
			    System.out.println("Message is ready");
			    if(!recipent.equals("")) {
		    	  		Transport.send(msg);  
			    		System.out.println("EMail Sent Successfully to "+ recipent+"!!");
			    }
			}

		} catch (MessagingException | UnsupportedEncodingException e) {
			System.out.print("Run time exception. Email cound not be sent");
			return false;
		}
		return true;
		
    }
    public boolean sendEmailWithAttachment(ArrayList<String> recipents, String subject, String text, String[] filePaths) {
    	if(Configurator.getIsServer())
    		return true;
    	
    	final String username = "support@orderon.co.in";
		final String password = "TOMTech$1234";

		Properties props = new Properties();
		
		props.put("mail.smtp.host", "smtpout.asia.secureserver.net"); //SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
		props.put("mail.smtp.port", "3535"); //SMTP Port

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage msg = new MimeMessage(session);
			
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		    msg.addHeader("format", "flowed");
		    msg.addHeader("Content-Transfer-Encoding", "8bit");
			
		    msg.setFrom(new InternetAddress(username, "NoReply-OrderOn"));

		    msg.setReplyTo(InternetAddress.parse("m@orderon.co.in", false));

		    msg.setSubject(subject, "UTF-8");
		
		    //3) create MimeBodyPart object and set your message text     
		    BodyPart messageBodyPart1 = new MimeBodyPart();  
		    messageBodyPart1.setContent(text, "text/html; charset=utf-8");  

		    //5) create Multipart object and add MimeBodyPart objects to this object
		    Multipart multipart = new MimeMultipart();  
		    multipart.addBodyPart(messageBodyPart1);  

		    //4) create new MimeBodyPart object and set DataHandler object to this object      
		    MimeBodyPart messageBodyPart2 = new MimeBodyPart();  
		    int locationLen = Configurator.getDownloadLocation().length()-1;
		    for (String filename : filePaths) {
		    	messageBodyPart2 = new MimeBodyPart(); 
		    	System.out.println(filename);
			    DataSource source = new FileDataSource(filename);  
			    messageBodyPart2.setDataHandler(new DataHandler(source));  
			    messageBodyPart2.setFileName(filename.substring(locationLen));  
			    //6) set the multipart object to the message object 
			    multipart.addBodyPart(messageBodyPart2); 
			}   
		      
		    msg.setContent(multipart);  

		    for (String recipent : recipents) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipent, false));
				
		    
			    System.out.println("Message is ready");
			    if(!recipent.equals("")) {
		    	  		Transport.send(msg);  
			    		System.out.println("EMail Sent Successfully to "+ recipent+"!!");
			    }
			}

		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return true;
    }
}
