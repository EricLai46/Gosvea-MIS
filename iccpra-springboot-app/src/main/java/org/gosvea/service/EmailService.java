package org.gosvea.service;

import org.gosvea.pojo.Venue;

public interface EmailService {

    void sendEmailNotification(String to, String subject, String text);


    void noticeIcpisManagerAddAD(String to,Venue venue);


    void noticeIcpisManagerAdWired(Venue venue);
}
