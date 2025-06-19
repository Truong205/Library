package com.example.libraryproject.entity;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Quản trị viên"),
    LIBRARIAN ("Thủ thư");

    private final String displayName;
    Role(String displayName) {
        this.displayName = displayName;
    }
}
