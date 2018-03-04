package com.worldunion.springregistration.controller;

import com.worldunion.springregistration.dto.UserDto;
import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.entity.VerificationToken;
import com.worldunion.springregistration.error.UserAlreadyExistException;
import com.worldunion.springregistration.event.OnRegistrationCompleteEvent;
import com.worldunion.springregistration.exception.EmailExistsException;
import com.worldunion.springregistration.service.IUserService;
import com.worldunion.springregistration.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;

@Controller
@RequestMapping(value = "/")
public class RegistrationController
{
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private IUserService service;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    MessageSource messageSources;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }


    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(
            @Valid UserDto accountDto, HttpServletRequest request) {
        logger.debug("Registering user account with information: {}", accountDto);
        User registered = createUserAccount(accountDto);
        if (registered == null) {
            throw new UserAlreadyExistException();
        }
        String appUrl = "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();

        eventPublisher.publishEvent(
                new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));

        return new GenericResponse("success");
    }

    private User createUserAccount(UserDto accountDto, BindingResult result) {
        User registered;
        try {
            registered = service.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {

            return null;
        }
        return registered;
    }

    @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(
            Locale locale, Model model, @RequestParam("token") String token) {
        VerificationToken verificationToken = service.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messageSources.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messageSources.getMessage("auth.message.expired", null, locale));
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        service.saveRegisteredUser(user);
        model.addAttribute("message", messageSources.getMessage("message.accountVerified", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }




    @GetMapping(value = "/user/resendRegistration")
    @ResponseBody
    public GenericResponse resendRegistrationToken(HttpServletRequest request, @RequestParam("token") String existingToken)
    {


        VerificationToken newToken = service.generateNewVerificationToken(existingToken);

        User user = service.getUser(newToken.getToken());

        String appUrl = request.getServerName()+":"+request.getServerPort()+ request.getContextPath();

        SimpleMailMessage simpleMailMessage = contructResendVerificationEmail(appUrl,request.getLocale(),newToken,user);

        mailSender.send(simpleMailMessage);

    return new GenericResponse(
            messageSources.getMessage("message.resendToken", null, request.getLocale()));
    }


    private SimpleMailMessage contructResendVerificationEmail
            (String contextPath, Locale locale, VerificationToken newToken, User user) {
        String confirmationUrl =
                contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
        String message = messageSources.getMessage("message.resendToken", null, locale);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " rn" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        email.setTo(user.getEmail());
        return email;
    }
}
