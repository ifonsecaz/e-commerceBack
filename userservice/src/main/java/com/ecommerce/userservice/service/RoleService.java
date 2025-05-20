package com.ecommerce.userservice.service;

import com.ecommerce.userservice.entity.Role;
import com.ecommerce.userservice.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    
    public Role verifyRole(String role){
        Role res=roleRepository.findByRole(role);
        if(res!=null){
            return res;
        }
        return null;
    }
}
