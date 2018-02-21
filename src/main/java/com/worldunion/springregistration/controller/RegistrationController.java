package com.worldunion.springregistration.controller;

import com.worldunion.springregistration.dto.UserDto;
import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.entity.VerificationToken;
import com.worldunion.springregistration.event.OnRegistrationCompleteEvent;
import com.worldunion.springregistration.exception.EmailExistsException;
import com.worldunion.springregistration.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

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
    MessageSource messageSources;

    @RequestMapping(value = "/user/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST,consumes = "application/x-www-form-urlencoded")
    public ModelAndView registerUserAccount(
             @Valid UserDto accountDto,
            BindingResult result, WebRequest request, Errors errors) {
        User registered = new User();
          if(!result.hasErrors())
          {
              registered = createUserAccount(accountDto,result);

          }
          if (registered == null)
          {
              result.rejectValue("email","Registration error");
          }


          eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                  (registered, request.getLocale(), request.getContextPath()));
          if (result.hasErrors())
          {

              return new ModelAndView("registration","user",accountDto);
          }
          else
          {
              return new ModelAndView("successRegister","user",accountDto);
          }
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

    @GetMapping(value = "/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model,
                                      @RequestParam("token") String token)
    {
        Locale locale = request.getLocale();

        VerificationToken  verified = service.getVerificationToken(token);
        if(verified == null)
        {
            String message =  messageSources.getMessage("auth.message.invalidToken",null,locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }
        User user = verified.getUser();
        Calendar cal = Calendar.getInstance();
        if((verified.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0 )
        {
            String messageValue = messageSources.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        service.saveRegisteredUser(user);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }


}
