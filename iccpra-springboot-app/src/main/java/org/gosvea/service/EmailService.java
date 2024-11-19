package org.gosvea.service;

import org.gosvea.pojo.Venue;

import java.util.List;

public interface EmailService {

    void sendEmailNotification(String to, String subject, String text);


    void noticeIcpisManagerAddAD(String to,Venue venue);


    void noticeIcpisManagerAdWired(Venue venue);

    void noticeJurinAddAd(String city,String date);

    void noticeJurinChangedAD(String city, String date);
}
