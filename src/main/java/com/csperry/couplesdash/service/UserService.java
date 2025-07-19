package com.csperry.couplesdash.service;

import com.csperry.couplesdash.model.Couple;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.repository.CoupleRepository;
import com.csperry.couplesdash.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    CoupleRepository coupleRepository;

    public ResponseEntity<?> linkUsers(String inviterEmail, String inviteeEmail){

        User inviter = userRepository.findByEmail(inviterEmail).orElseThrow();
        User invitee = userRepository.findByEmail(inviteeEmail).orElseThrow();

        if (inviter.getCouple() != null || invitee.getCouple() != null) {
            return ResponseEntity.badRequest().body("One or both users are already in a couple.");
        }

        Couple couple = coupleRepository.save(new Couple());

        inviter.setCouple(couple);
        invitee.setCouple(couple);
        userRepository.saveAll(List.of(inviter, invitee));

        return ResponseEntity.ok("Couple linked successfully.");
    }
}
