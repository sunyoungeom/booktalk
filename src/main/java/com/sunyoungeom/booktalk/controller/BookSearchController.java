package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.service.SearchService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sunyoungeom.booktalk.domain.Book;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/books")
public class BookSearchController {

    private final SearchService searchService;

    @Autowired
    public BookSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search/{query}/{page}")
    public String searchBook(@PathVariable(name = "query") String query, @PathVariable(name = "page") Integer page, Model model) {
        List<Book> books = searchService.search(query, page);
        model.addAttribute("bookList", books);
        model.addAttribute("query", "🔎 " + query);
        return "books/search";
    }

    @GetMapping("/detail/{title}")
    public String detail(@PathVariable(name = "title") String title, Model model, HttpSession session) {
        List<Book> detailBooks = searchService.search(title, 1);
        Book detailBook = detailBooks.get(0);
        model.addAttribute("detailBook", detailBook);
        model.addAttribute("title", title);
        session.setAttribute("title", title);
        log.info("테스트용 유저아이디");
        session.setAttribute("currentUser", "테스트용");
        return "books/detail";
    }
}
