package com.worldunion.springregistration.service;

import com.worldunion.springregistration.dto.UserDto;
import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.entity.VerificationToken;
import com.worldunion.springregistration.exception.EmailExistsException;

public interface IUserService {
    User registerNewUserAccount(UserDto accountDto)
            throws EmailExistsException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);


    VerificationToken generateNewVerificationToken(String existingToken);
}
