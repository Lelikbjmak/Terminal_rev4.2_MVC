package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.SeviceImplementation.VerificationTokenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailResendListener implements ApplicationListener<MailConfirmationResendEvent> {


    @Autowired
    VerificationTokenServiceImpl tokenService;

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(MailResendListener.class);

    @Override
    public void onApplicationEvent(MailConfirmationResendEvent event) {

        this.confirmRegistration(event);

    }

    @Transactional
    private void confirmRegistration(MailConfirmationResendEvent event) {

        User user = event.getUser(); // get user
        VerificationToken token = event.getToken();  // get users token

        tokenService.rebuildExistingToken(token); // extend expiry date to confirm again!

        String recipientAddress = user.getMail();
        String confirmationUrl = event.getAppUrl() + "/Barclays/client/registrationConfirm?token=" + token.getToken();

        //SimpleMailMessage email = new SimpleMailMessage(); // if simple message default text

        MimeMessage mimeMessage = mailSender.createMimeMessage();  // create mime message -> use html docs
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");


        String htmlMsg = "<a href = 'http://localhost:8080/Barclays'><img class='logomenu1' alt='Barclays Logo' src='https://www.vectorlogo.zone/logos/barclays/barclays-ar21.png' style='width: 275px; height: 150px; margin-top: -45px; display:block;'></a>" +
                "<div style=\"width: 250px; height: 50px; background-color: #0096f4; margin-left: auto; margin-right: auto; text-align: center;  border-radius: 40px; margin-top: 50px; margin-bottom: 50px;\" onmouseover=\"this.style.backgroundColor='#555';\" onmouseout=\"this.style.backgroundColor='#333';\">\n" +
                "    <a href=\"" + confirmationUrl + "\" style=' display: block; height: 100%; border-radius: 40px; padding: 10px; border:none; padding:0; margin:0; font-family:\"helvetica\",\"arial\",sans-serif; font-weight:700; line-height:50px; letter-spacing:0.15px; font-size:14px; text-decoration:none; text-align:center; text-transform:uppercase; color:#ffffff'>Verify account</a>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<table style=\"border-collapse:collapse;padding:0;background-color:#f7f7f7;width:100%\" dir=\"auto\"><tbody style=\"border:none;padding:0;margin:0\"><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0\"><img alt=\"Barclays Logo\" height=\"35\" style=\"display:block;max-width:100%;height:45px\" src='https://brandslogos.com/wp-content/uploads/images/large/barclays-logo-black-and-white-1.png'></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0\"><hr style=\"height:1px;background-color:#d1d5d9;border:none;margin:0px;padding:0px\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\">\n" +
                "    <td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Get Barclays for: &nbsp;<a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">iPhone</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">iPad</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">Android</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">Other</a></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0\"><hr style=\"height:1px;background-color:#d1d5d9;border:none;margin:0px;padding:0px\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\"><td style=\"border:none;padding:0;margin:0;width:6.25%\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">If you have questions or complaints, please <a href='http://localhost:8080/Barclays' style=\"text-decoration:none;color:rgb(109,109,109);border:none;margin:0px;padding:0px;font-weight:bold\" target=\"_blank\" data-saferedirecturl=\"#\">contact us</a>.</td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:33px\"><td colspan=\"3\" style=\"border:none;padding:8.25px;margin:0;height:33px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\"><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">Terms of Use</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"#\" target=\"_blank\" data-saferedirecturl=\"#\">Privacy Policy</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\">Contact Us</a></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Barclays UK, 2 Churchill Pl, London E14 5RB, United Kingdom</td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\"><td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr></tbody>\n" +
                "</table>";  // example of html String we'll send to consumer


        try {
            mimeMessage.setContent(htmlMsg, "text/html");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        try {

            mimeMessageHelper.setTo(recipientAddress);
            mimeMessageHelper.setSubject("Account Verification");
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
