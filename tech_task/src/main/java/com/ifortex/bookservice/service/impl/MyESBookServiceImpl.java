package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.service.BookService;

import java.util.List;
import java.util.Map;

public class MyESBookServiceImpl implements BookService {
    @Override
    public Map<String, Long> getBooks() {
        return null;
    }

    @Override
    public List<Book> getAllByCriteria(SearchCriteria searchCriteria) {
        return null;
    }
    /*I created this class to implement the BookService interface
 and place my implementation in the existing impl package*/
}
