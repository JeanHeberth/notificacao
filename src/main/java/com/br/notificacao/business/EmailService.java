package com.br.notificacao.business;

import com.br.notificacao.business.dto.TarefasDTO;
import com.br.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    public String nomeRemetente;


    public void enviaEmail(TarefasDTO tarefa) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setFrom(new InternetAddress(remetente, nomeRemetente));
            mimeMessageHelper.setTo(tarefa.getEmailUsuario());
            mimeMessageHelper.setSubject("Notificação de tarefa");

            Context context = new Context();
            context.setVariable("nomeTarefa", tarefa.getNomeTarefa());
            context.setVariable("dataEvento", tarefa.getDataEvento());
            context.setVariable("descricao", tarefa.getDescricao());

            String templateEmail = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(templateEmail, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("Erro ao enviar email", e.getCause());
        }
    }
}
