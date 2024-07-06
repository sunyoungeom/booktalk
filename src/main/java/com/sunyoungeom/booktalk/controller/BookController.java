package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.domain.Book;
import com.sunyoungeom.booktalk.scheduler.BestsellerScheduler;
import com.sunyoungeom.booktalk.service.SearchService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
@Slf4j
@RequiredArgsConstructor
public class BookController {

    private final SearchService searchService;

    @GetMapping("/search")
    public String getBooksSearch(Model model) throws IOException {
        BestsellerScheduler bestsellerScheduler = new BestsellerScheduler();
        List<Map<String, String>> bestseller
                = bestsellerScheduler.loadBestsellerFromJson();
        model.addAttribute("bookList", bestseller);
        model.addAttribute("query", "베스트셀러 TOP20");
        return "books/search";
    }

    @GetMapping("/search/{query}")
    public String searchBook(@PathVariable(name = "query") String query, Model model) {
        List<Book> books = searchService.search(query, 1);
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
        log.info("title: {}", title);
        return "books/detail";
    }
}