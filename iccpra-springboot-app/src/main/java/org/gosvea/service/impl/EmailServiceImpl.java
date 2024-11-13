package org.gosvea.service.impl;

import org.gosvea.pojo.Icpie;
import org.gosvea.pojo.Venue;
import org.gosvea.service.EmailService;
import org.gosvea.service.IccpraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private IccpraService iccpraService;
    @Override
    public void sendEmailNotification(String to, String subject, String text) {
        SimpleMailMessage message=new SimpleMailMessage();
        if(to!=null)
        {
            message.setTo(to);
        }
        else{
            System.out.println("send to is null");
        }
        message.setSubject(subject);
        message.setText(text);
        //message.setFrom();
        mailSender.send(message);
    }
   //通知ICPIS补打广告
    @Override
    public void noticeIcpisManagerAddAD(String to, Venue venue) {
        SimpleMailMessage message=new SimpleMailMessage();
        if(to!=null)
        {
            message.setTo(to);
        }
        else {
            System.out.println("send to is null");
        }
        message.setSubject("***重要消息补打广告通知***");
        message.setText("请给"+venue.getCity()+"点,补打广告");
        mailSender.send(message);
    }
    //广告未打异常通知
    @Override
    public void noticeIcpisManagerAdWired(Venue venue) {
        SimpleMailMessage message=new SimpleMailMessage();
        if(venue.getIcpisManager()!=null&&!venue.getIcpisManager().isEmpty())
        {
            Icpie icpie=iccpraService.findByIcpieName(venue.getIcpisManager());
            String to=icpie.getEmail();
            if(to!=null)
            {
                message.setTo(to);
            }
            else {
                System.out.println("send to is null");
            }
            message.setCc("andyli@usjus.org");
            message.setSubject("***广告异常通知***");
            message.setText("注意："+venue.getCity()+"点,广告异常，请检查");
            mailSender.send(message);
        }


    }


}
