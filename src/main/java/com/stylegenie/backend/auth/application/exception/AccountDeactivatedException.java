package com.stylegenie.backend.auth.application.exception;

public class AccountDeactivatedException extends RuntimeException {
    public AccountDeactivatedException() {
        super("Account is deactivated");
    }
}
