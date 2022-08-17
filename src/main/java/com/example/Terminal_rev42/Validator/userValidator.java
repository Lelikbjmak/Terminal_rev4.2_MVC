package com.example.Terminal_rev42.Validator;

import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Servicies.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class userValidator implements Validator {

    @Autowired
    userService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        user user = (user) object;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Required");
        if(user.getUsername().length() < 6){
            errors.rejectValue("username", "Size.userForm.username");
        }

        if(userService.findByUsername(user.getUsername()) != null){
            errors.rejectValue("username", "Duplicate.userForm.password");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Required");
        if(user.getPassword().length() < 8){
            errors.rejectValue("password", "Size.userForm.password");
        }

        if(!user.getConfirmedpassword().equals(user.getPassword())){
            errors.rejectValue("password", "Different.userForm.password");
        }
    }
}
