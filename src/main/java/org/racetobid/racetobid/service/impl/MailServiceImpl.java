package org.racetobid.racetobid.service.impl;

import org.racetobid.racetobid.service.MailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {
    @Override
    public String sendMail(String to, String subject, String body) {
        return "";
    }

    @Override
    public String sendMailWithAttachment(String to, String subject, String body, String attachmentPath) {
        return "";
    }

    @Override
    public String sendMailWithMultipleAttachments(String to, String subject, String body, List<String> attachmentPaths) {
        return "";
    }

    @Override
    public String sendMailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateData) {
        return "";
    }

    @Override
    public String sendMailWithTemplateAndAttachment(String to, String subject, String templateName, Map<String, Object> templateData, String attachmentPath) {
        return "";
    }

    @Override
    public String sendMailWithTemplateAndMultipleAttachments(String to, String subject, String templateName, Map<String, Object> templateData, List<String> attachmentPaths) {
        return "";
    }
}
