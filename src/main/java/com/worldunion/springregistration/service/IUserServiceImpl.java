package com.worldunion.springregistration.service;

import com.worldunion.springregistration.dto.UserDto;
import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.entity.VerificationToken;
import com.worldunion.springregistration.exception.EmailExistsException;
import com.worldunion.springregistration.repository.RoleRepository;
import com.worldunion.springregistration.repository.UserRepository;
import com.worldunion.springregistration.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.TransactionScoped;
import java.util.Arrays;

@Service
public class IUserServiceImpl implements IUserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Override
    @Transactional
    public User registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException(
                    "There is an account with that email address: " + accountDto.getEmail());
        }
        User user = new User();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(accountDto.getPassword());
        user.setEmail(accountDto.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return repository.save(user);


    }

    @Override
    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public void saveRegisteredUser(User user) {
         repository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String existingToken) {
        VerificationToken token = getVerificationToken(existingToken);
        token.updateToken();
        tokenRepository.save(token);
        return token;
    }

    private boolean emailExist(String email) {

        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }


}
