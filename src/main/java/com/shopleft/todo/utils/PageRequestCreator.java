package com.shopleft.todo.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageRequestCreator {

    public static Pageable createPageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }
    
    public static Pageable createPageRequest(int pageNumber) {
        return PageRequest.of(pageNumber,5);
    }

    public static Pageable createPageRequest() {
        return PageRequest.of(0,5);
    }   
}
