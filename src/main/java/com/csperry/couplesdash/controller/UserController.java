package com.csperry.couplesdash.controller;

import com.csperry.couplesdash.DTO.LinkRequest;
import com.csperry.couplesdash.model.AuthRequest;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.repository.UserRepository;
import com.csperry.couplesdash.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.csperry.couplesdash.constants.APIConstants.LINK_ENDPOINT;
import static com.csperry.couplesdash.constants.APIConstants.USER_URL;

@RestController
@CrossOrigin
@RequestMapping(USER_URL)
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @PostMapping(LINK_ENDPOINT)
    public String linkUsers(@RequestBody LinkRequest request, Authentication authentication) {
        String inviterEmail = authentication.getName();
        String inviteeEmail = request.getInviteeEmail();

        logger.debug("Inviter from authentication: {}", inviterEmail);
        User inviter = (User) userRepository.findByEmailIgnoreCase(inviterEmail)
                .orElseThrow(() -> {
                    logger.error("No user found for email: {}", inviterEmail);
                    return new RuntimeException("Inviter not found");
                });

        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("Invitee not found"));

        if (invitee.isLinked()) {
            throw new RuntimeException("Invitee is already linked");
        }

        invitee.setCouple(inviter.getCouple());
        invitee.setLinked(true);
        inviter.setLinked(true);

        userRepository.save(invitee);
        userRepository.save(inviter);

        return "User linked successfully";
    }

}
