package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role, Long> {

    Role findByRole(String role);
}
