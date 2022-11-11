package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.investments;
import com.example.Terminal_rev42.Model.user;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class NotifyValidityListener {

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(NotifyValidityListener.class);

    @EventListener
    @Transactional
    public void handleNotifyEventBill(NotifyBillValidityExpirationEvent event){

        logger.info("Notify users about bill validation expiration active");

        bill bill = event.getBill();

        user user = bill.getClient().getUser();
        String recipientAddress = user.getMail();

        MimeMessage mimeMessage = mailSender.createMimeMessage();  // create mime message -> use html docs
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMsg =
                "<div style='width: 35%; position:relative; margin-left: auto; margin-right: auto;'>" +
                "<a align='left' href = 'http://localhost:8080/Barclays'><img class='logomenu1' alt='Barclays Logo' src='https://www.vectorlogo.zone/logos/barclays/barclays-ar21.png' style='width: 230px; height: 115px; margin-top: -45px; display:block;'></a>" +
                "<p align='left' style='border:none;padding:0; padding-bottom: 25px; padding-left: 25px; margin:0; background-color:#ffffff;color:#000000;font-size:28px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Bill expiration date.\n" +
                "</p>" +
                "<div><p align='left' style='border:none; margin-bottom: 60px; padding-left: 25px; background-color:#ffffff;color:#000000;font-size:16px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Dear " + "<i>"+ user.getUsername() + "</i>" + ", your bill " + "<b>" + bill.getCard() + " <i>" + bill.getCurrency() + "</i>" + "</b> " + "(" +bill.getType() + ")" + " will be expired in " + event.getDays() + " days (" + bill.getValidity() + ")" + ".<br>Please withdraw all funds from check to beware confusing situations." +
                "</p></div>\n" +
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
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Get Barclays for: &nbsp;<a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2S6HMUbmBoGF2GOAsxjvj6\">iPhone</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1bZct3SwcIU9dLGPDyXqhJ\">iPad</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1i6vBxQJFRIzOcTsfXSRaf\">Android</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2fDQKHtgoe7kBdLYTaCUec\">Other</a></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0\"><hr style=\"height:1px;background-color:#d1d5d9;border:none;margin:0px;padding:0px\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\"><td style=\"border:none;padding:0;margin:0;width:6.25%\"></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">If you have questions or complaints, please <a href='http://localhost:8080/Barclays' style=\"text-decoration:none;color:rgb(109,109,109);border:none;margin:0px;padding:0px;font-weight:bold\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnqoSNgGIkCHz6APQS2LTpoBZNcIY7hi_fB29aXxnYN7hhCq7OcyL3YFxYCd4aBcTtg/3pu/aELHK-PVSCCppkIBMas8ag/h6/jR9bx9XhEN08VvbdBf_EsjD9xAv-fJKkrxg704Bj1DM&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2OAdBKM14bViv0BB9JIXZG\">contact us</a>.</td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:33px\"><td colspan=\"3\" style=\"border:none;padding:8.25px;margin:0;height:33px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\"><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1eeZKQw9l4gL4hkTXZFZ5K\">Terms of Use</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw3Jxh1QhXzPnWwdVLVx0F-8\">Privacy Policy</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\">Contact Us</a></td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Barclays UK, 2 Churchill Pl, London E14 5RB, United Kingdom</td>\n" +
                "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\"><td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr></tbody>\n" +
                "</table>" + "\n</div>";  // example of html String we'll send to consumer

        try {
            mimeMessage.setContent(htmlMsg, "text/html");
        } catch (MessagingException e) {
            logger.error("Error in setting MimeMessageHelper to HTML text type! " + recipientAddress);
            throw new RuntimeException(e);
        }

        try {

            mimeMessageHelper.setTo(recipientAddress);
            mimeMessageHelper.setSubject("Bill expiration");
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Issue in setting recipient and sender of mail: " + recipientAddress);
            throw new RuntimeException(e);
        }

        logger.info("Notification about expiration date of bill has sent to " + user.getUsername() + "!");
    }

    @EventListener
    @Transactional
    public void handleNotifyEventOutOfValidityBill(NotifyBillValidityExpirationEvent event){

        System.err.println("Notify users about bill validation expiration");
        bill bill = event.getBill();

        user user = bill.getClient().getUser();
        String recipientAddress = user.getMail();

        MimeMessage mimeMessage = mailSender.createMimeMessage();  // create mime message -> use html docs
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMsg =
                "<div style='width: 35%; position:relative; margin-left: auto; margin-right: auto;'>" +
                        "<a align='left' href = 'http://localhost:8080/Barclays'><img class='logomenu1' alt='Barclays Logo' src='https://www.vectorlogo.zone/logos/barclays/barclays-ar21.png' style='width: 230px; height: 115px; margin-top: -45px; display:block;'></a>" +
                        "<p align='left' style='border:none;padding:0; padding-bottom: 25px; padding-left: 25px; margin:0; background-color:#ffffff;color:#000000;font-size:28px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Bill expiration date.\n" +
                        "</p>" +
                        "<div><p align='left' style='border:none; margin-bottom: 60px; padding-left: 25px; background-color:#ffffff;color:#000000;font-size:16px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Dear " + "<i>"+ user.getUsername() + "</i>" + ", your bill " + "<b>" + bill.getCard() + " <i>" + bill.getCurrency() + "</i>" + "</b> " + "(" +bill.getType() + ")" + " was expired, validity " + bill.getValidity() + ".<br>Your card is inactive, all transactions are halted. " +
                        "</p></div>\n" +
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
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Get Barclays for: &nbsp;<a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2S6HMUbmBoGF2GOAsxjvj6\">iPhone</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1bZct3SwcIU9dLGPDyXqhJ\">iPad</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1i6vBxQJFRIzOcTsfXSRaf\">Android</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2fDQKHtgoe7kBdLYTaCUec\">Other</a></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0\"><hr style=\"height:1px;background-color:#d1d5d9;border:none;margin:0px;padding:0px\"></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                        "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\"><td style=\"border:none;padding:0;margin:0;width:6.25%\"></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">If you have questions or complaints, please <a href='http://localhost:8080/Barclays' style=\"text-decoration:none;color:rgb(109,109,109);border:none;margin:0px;padding:0px;font-weight:bold\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnqoSNgGIkCHz6APQS2LTpoBZNcIY7hi_fB29aXxnYN7hhCq7OcyL3YFxYCd4aBcTtg/3pu/aELHK-PVSCCppkIBMas8ag/h6/jR9bx9XhEN08VvbdBf_EsjD9xAv-fJKkrxg704Bj1DM&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2OAdBKM14bViv0BB9JIXZG\">contact us</a>.</td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:33px\"><td colspan=\"3\" style=\"border:none;padding:8.25px;margin:0;height:33px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\"><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1eeZKQw9l4gL4hkTXZFZ5K\">Terms of Use</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw3Jxh1QhXzPnWwdVLVx0F-8\">Privacy Policy</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\">Contact Us</a></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Barclays UK, 2 Churchill Pl, London E14 5RB, United Kingdom</td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\"><td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr></tbody>\n" +
                        "</table>" + "\n</div>";  // example of html String we'll send to consumer

        try {
            mimeMessage.setContent(htmlMsg, "text/html");
        } catch (MessagingException e) {
            System.err.println("Error in setting MimeMessageHelper to HTML text type!");
            throw new RuntimeException(e);
        }

        try {

            mimeMessageHelper.setTo(recipientAddress);
            mimeMessageHelper.setSubject("Bill expiration");
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            System.err.println("Issue in setting recipient and sender of mail");
            throw new RuntimeException(e);
        }


        System.err.println("notification about expiration date of bill has sent to " + user.getUsername() + "!");
    }


    @EventListener
    @Transactional
    public void handleNotifyEventOutOfValidityInvests(NotifyAboutInvestExpirationEvent event){

        logger.info("Notify users about validation expiration");
        investments investments = event.getInvestments();

        user user = investments.getClient().getUser();
        String recipientAddress = user.getMail();

        MimeMessage mimeMessage = mailSender.createMimeMessage();  // create mime message -> use html docs
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMsg =
                "<div style='width: 35%; position:relative; margin-left: auto; margin-right: auto;'>" +
                        "<a align='left' href = 'http://localhost:8080/Barclays'><img class='logomenu1' alt='Barclays Logo' src='https://www.vectorlogo.zone/logos/barclays/barclays-ar21.png' style='width: 230px; height: 115px; margin-top: -45px; display:block;'></a>" +
                        "<p align='left' style='border:none;padding:0; padding-bottom: 25px; padding-left: 25px; margin:0; background-color:#ffffff;color:#000000;font-size:28px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Holding expiration date.\n" +
                        "</p>" +
                        "<div><p align='left' style='border:none; margin-bottom: 60px; padding-left: 25px; background-color:#ffffff;color:#000000;font-size:16px;font-family:\"helvetica\",\"arial\",sans-serif!important;font-weight:400;'>Dear " + "<i>"+ user.getUsername() + "</i>" + ", your invest# " + "<b>" + investments.getId() + " <i>" + investments.getCurrency() + "</i>" + "</b> " + "(" + investments.getType() + ")" + " was expired, validity " +investments.getBegin().plusMonths(investments.getTerm()) + ".<br>Your holding is inactive, withdraw all your funds. " +
                        "</p></div>\n" +
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
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Get Barclays for: &nbsp;<a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h2/Qx5tbHkyxqq2YYskPAWtEx510gYxop5LH_lLKV68Xm8&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2S6HMUbmBoGF2GOAsxjvj6\">iPhone</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/YHCG2buDf8JvWpMQNG2As5amV2GC27XBzPSsU7APvx-6DTQobH7TamJ2I1d9JbcKPy3J6ej35OIJ9JAYsjUeiw/3pu/aELHK-PVSCCppkIBMas8ag/h3/_9LcUfL5GZWZJQsGDrjnfPeVaM61FjJqFmObYJ1E9n0&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1bZct3SwcIU9dLGPDyXqhJ\">iPad</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/y-6_mmgicre6Ll8XSk96fBnmPHFiVmVsXsrbTc1Zm5ih8xpjjjXct5u84ohiOCY5ZA-pRaeH-FVU1LxCngZcwykZmUhcSXElkVCtjQ_u0Qg/3pu/aELHK-PVSCCppkIBMas8ag/h4/8PNBKU8LECQXebTpT_tSxG-_3mZ2u6UjgUFSrWzSSUE&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1i6vBxQJFRIzOcTsfXSRaf\">Android</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm6VtlPyfW5wyZExJ71OU9z9AGOfTUBTPP4CoM2Yhaef/3pu/aELHK-PVSCCppkIBMas8ag/h5/7-ADHoNtG921vqMqVix1fzSAzMHKDYw6ht9iioBvZm4&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2fDQKHtgoe7kBdLYTaCUec\">Other</a></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0\"><hr style=\"height:1px;background-color:#d1d5d9;border:none;margin:0px;padding:0px\"></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\">\n" +
                        "    <td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\"><td style=\"border:none;padding:0;margin:0;width:6.25%\"></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">If you have questions or complaints, please <a href='http://localhost:8080/Barclays' style=\"text-decoration:none;color:rgb(109,109,109);border:none;margin:0px;padding:0px;font-weight:bold\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnqoSNgGIkCHz6APQS2LTpoBZNcIY7hi_fB29aXxnYN7hhCq7OcyL3YFxYCd4aBcTtg/3pu/aELHK-PVSCCppkIBMas8ag/h6/jR9bx9XhEN08VvbdBf_EsjD9xAv-fJKkrxg704Bj1DM&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw2OAdBKM14bViv0BB9JIXZG\">contact us</a>.</td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:33px\"><td colspan=\"3\" style=\"border:none;padding:8.25px;margin:0;height:33px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\"><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdBvMF1cejiEx5MBw90_1UD1uEU57nnnAZzEyj1Cjkmxg/3pu/aELHK-PVSCCppkIBMas8ag/h7/23coLAVQf99KCqhqNtMR8Awik3wXB_ib76k1E6H1uDA&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw1eeZKQw9l4gL4hkTXZFZ5K\">Terms of Use</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\" href=\"https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://wl.spotify.com/ss/c/giTx_g04qHY_Ilr_AL-xnm73xnPLXFSNA-VpN8-njZdO1dmn03Lws5vM1tluV3oyOkuvYo_zk3KHW8HlR7msUQ/3pu/aELHK-PVSCCppkIBMas8ag/h8/LiuqJd2KlbEplRT3jrSPeky0OL5flPegYJ8BX96hU0o&amp;source=gmail&amp;ust=1665994544560000&amp;usg=AOvVaw3Jxh1QhXzPnWwdVLVx0F-8\">Privacy Policy</a><span style=\"border:none;padding:4px 0;margin:0 7px;width:1px;border-left:solid 1px #c3c3c3;border-right:solid 1px transparent\">&nbsp;</span><a style=\"text-decoration:none;color:#6d6d6d;display:inline-block;font-weight:700\">Contact Us</a></td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:12px\"><td colspan=\"3\" style=\"border:none;padding:3px;margin:0;height:12px\"></td></tr><tr style=\"border:none;margin:0px;padding:0px\">\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td><td style=\"border:none;padding:0;margin:0;font-family:&quot;helvetica&quot;,&quot;arial&quot;,sans-serif;font-weight:400;line-height:1.65em;letter-spacing:0.15px;font-size:11px;text-decoration:none;color:#88898c\">Barclays UK, 2 Churchill Pl, London E14 5RB, United Kingdom</td>\n" +
                        "    <td style=\"border:none;padding:0;margin:0;width:6.25%\"></td></tr><tr style=\"border:none;margin:0px;padding:0px;height:25px\"><td colspan=\"3\" style=\"border:none;padding:6.25px;margin:0;height:25px\"></td></tr></tbody>\n" +
                        "</table>" + "\n</div>";  // example of html String we'll send to consumer

        try {
            mimeMessage.setContent(htmlMsg, "text/html");
        } catch (MessagingException e) {
            logger.error("Error in setting MimeMessageHelper to HTML text type!");
            throw new RuntimeException(e);
        }

        try {

            mimeMessageHelper.setTo(recipientAddress);
            mimeMessageHelper.setSubject("Holding expiration");
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Issue in setting recipient and sender of mail");
            throw new RuntimeException(e);
        }


        logger.info("notification about expiration date of bill has sent to " + user.getUsername() + "!");
    }


}
