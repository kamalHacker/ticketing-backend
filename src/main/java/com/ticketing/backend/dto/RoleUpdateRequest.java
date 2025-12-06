package com.ticketing.backend.dto;
import com.ticketing.backend.model.Role;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    private Role role;
}