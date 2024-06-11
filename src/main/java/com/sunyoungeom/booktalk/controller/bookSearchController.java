package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sunyoungeom.booktalk.domain.Book;
import java.util.List;

@Slf4j
@Controller
public class bookSearchController {

    private final SearchService searchService;

    @Autowired
    public bookSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/books/search/{query}/{page}")
    public String searchBook(@PathVariable String query, @PathVariable Integer page, Model model) {
//        return searchService.search(query);
        List<Book> books = searchService.search(query, page);
        model.addAttribute("bookList", books);
        model.addAttribute("query", "🔎 " +query);
        System.out.println("bookloist: " + books.toString());
        return "books/search";
    }
}
/*
 @RequestMapping("/search/{keyword}")
    public String searchBooks(@PathVariable String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        System.out.println(keyword);
        return "books/search";
    }
 */
