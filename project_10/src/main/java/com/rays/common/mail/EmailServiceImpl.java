package com.rays.common.mail;

import java.io.File;

import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.rays.common.UserContext;
import com.rays.common.attachment.AttachmentDTO;
import com.rays.common.attachment.AttachmentServiceInt;
import com.rays.common.message.MessageDTO;
import com.rays.common.message.MessageServiceInt;

/**
 * Provides email services
 * Dilip Malav 
 */
@Component
public class EmailServiceImpl {

	/**
	 * Send email
	 */
	@Autowired
	public JavaMailSender emailSender;

	/**
	 * Get messages from database
	 */
	@Autowired
	public MessageServiceInt messageService;

	/**
	 * Get attached filed by ids
	 */
	@Autowired
	private AttachmentServiceInt attachmentService;

	/**
	 * Sends an email
	 * 
	 * @param dto
	 * @param ctx
	 */
	public void send(EmailDTO dto, UserContext ctx) {

	    if (dto.getMessageCode() != null) {
	        MessageDTO messageDTO = messageService.findByCode(dto.getMessageCode(), ctx);

	        if (messageDTO == null || "Inactive".equals(messageDTO.getStatus())) {
	            return;
	        }

	        dto.setSubject(messageDTO.getSubject(dto.getMessageParams()));
	        dto.setBody(messageDTO.getBody(dto.getMessageParams()));
	        dto.setIsHTML("Y".equals(messageDTO.getHtml()));
	    }

	    MimeMessage message = emailSender.createMimeMessage();

	    try {
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        if (!dto.getTo().isEmpty()) {
	            helper.setTo(dto.getTo().toArray(new String[0]));
	        }

	        if (!dto.getCc().isEmpty()) {
	            helper.setCc(dto.getCc().toArray(new String[0]));
	        }

	        if (!dto.getBcc().isEmpty()) {
	            helper.setBcc(dto.getBcc().toArray(new String[0]));
	        }

	        helper.setSubject(dto.getSubject());
	        helper.setText(dto.getBody(), dto.getIsHTML());

	        // Attach files from file system
	        for (String path : dto.getAttachedFilePath()) {
	            FileSystemResource file = new FileSystemResource(new File(path));
	            helper.addAttachment(file.getFilename(), file);
	        }

	        // Attach files from database
	        for (Long id : dto.getAttachedFileId()) {
	            AttachmentDTO fileDto = attachmentService.findById(id, ctx);
	            if (fileDto != null) {
	                ByteArrayResource file = new ByteArrayResource(fileDto.getDoc());
	                helper.addAttachment(fileDto.getName(), file);
	            }
	        }

	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return;
	    }

	    // Send email asynchronously
	    new Thread(() -> emailSender.send(message)).start();
	}

}
