package com.project.luvsick.service;

import com.project.luvsick.dto.LoginRequestDTO;
import org.springframework.stereotype.Service;


public interface AuthService {
    public String authenticate(String email,String password);
}
