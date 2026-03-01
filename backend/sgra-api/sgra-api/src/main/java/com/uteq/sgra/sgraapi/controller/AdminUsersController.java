package com.uteq.sgra.sgraapi.controller;

import com.uteq.sgra.sgraapi.dto.CreateUserRequest;
import com.uteq.sgra.sgraapi.dto.UpdateRolesRequest;
import com.uteq.sgra.sgraapi.dto.UserDto;
import com.uteq.sgra.sgraapi.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {

    private final AdminUserService service;

    @GetMapping
    public List<UserDto> list() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDto> updateRoles(@PathVariable Long id, @RequestBody UpdateRolesRequest req) {
        return ResponseEntity.ok(service.updateRoles(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
