package com.example.Blasira_Backend.dto.admin;

import com.example.Blasira_Backend.model.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UpdateUserRolesRequest {
    private List<Role> roles;
}
