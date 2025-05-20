package com.ecommerce.userservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.ecommerce.userservice.entity.EmailChangeRequest;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Scheduled(fixedRate = 3600000) // Runs hourly
    @Transactional
    public void checkPendingEmailValidations() {
        // Find users with unverified emails
        List<User> users = userRepository.findByAcvalidatedFalse();
        
        for (User user : users) {
            if (user.getLastPasswordReset().plusMinutes(20).isBefore(LocalDateTime.now())) {
                // Revert to old email
                if(user.getOldEmail()!=null){
                    user.setEmail(user.getOldEmail());
                    user.setAcvalidated(true);
                    user.setLastPasswordReset();
                    userRepository.save(user);
                }
                else{
                    user.removeRole();
                    userRepository.delete(user);
                }
            }
        }
    }
}
