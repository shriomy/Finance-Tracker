package com.example.finance_tracker.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    // Method to send an email
    public void sendEmail(String toEmail, String subject, String content) {
        Email from = new Email("no-reply@financetracker.com"); // Replace with your sender email
        Email to = new Email("nimashafdop@gmail.com"); // Recipient email
        Content emailContent = new Content("text/plain", content); // Email content (plain text)

        Mail mail = new Mail(from, subject, to, emailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            // Log the response status and body
            System.out.println("Email sent! Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
        } catch (IOException ex) {
            System.err.println("Failed to send email: " + ex.getMessage());
        }
    }
}