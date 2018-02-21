package com.worldunion.springregistration.dto;

import com.worldunion.springregistration.validate.PasswordMatches;
import com.worldunion.springregistration.validate.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserDto {
    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty

    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;

    // standard getters and setters
}
