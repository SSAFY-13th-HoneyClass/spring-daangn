package org.example.springboot.exception;

public class ItemNotFoundException extends RuntimeException {
    
    public ItemNotFoundException(String message) {
        super(message);
    }
    
    public ItemNotFoundException(Long itemId) {
        super("Item not found with id: " + itemId);
    }
} 