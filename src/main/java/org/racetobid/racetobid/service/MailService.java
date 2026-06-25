package org.racetobid.racetobid.service;

import java.util.List;
import java.util.Map;

public interface MailService {
    String sendMail(String to, String subject, String body);
    String sendMailWithAttachment(String to, String subject, String body, String attachmentPath);
    String sendMailWithMultipleAttachments(String to, String subject, String body, List<String> attachmentPaths);
    String sendMailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateData);
    String sendMailWithTemplateAndAttachment(String to, String subject, String templateName, Map<String, Object> templateData, String attachmentPath);
    String sendMailWithTemplateAndMultipleAttachments(String to, String subject, String templateName, Map<String, Object> templateData, List<String> attachmentPaths);


}
