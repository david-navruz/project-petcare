package com.project.petcare.service.user;

import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAttributesMapper {
    public void setCommonAttributes(RegistrationRequest source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setGender(source.getGender());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setEnabled(source.isEnabled());
        target.setUserType(source.getUserType());
    }
}
