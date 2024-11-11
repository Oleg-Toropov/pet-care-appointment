package com.olegtoropoff.petcareappointment.event;

import com.olegtoropoff.petcareappointment.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;

    public RegistrationCompleteEvent(User user) {
        super(user);
        this.user = user;
    }
}
