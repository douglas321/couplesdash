package com.csperry.couplesdash.DTO;

import com.csperry.couplesdash.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private Long coupleId;
    private boolean linked;

    public UserDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.coupleId = user.getCouple() != null ? user.getCouple().getId() : null;
        this.linked = user.isLinked();
    }
}
