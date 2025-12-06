package com.ticketing.backend.dto;

import com.ticketing.backend.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}
