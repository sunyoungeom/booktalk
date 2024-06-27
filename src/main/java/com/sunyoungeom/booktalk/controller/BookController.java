package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.scheduler.BestsellerScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
@Slf4j
public class BookController {
    @GetMapping("/search")
    public String getBooksSearch(Model model) throws IOException {
        BestsellerScheduler bestsellerScheduler = new BestsellerScheduler();
        List<Map<String, String>> bestseller
                = bestsellerScheduler.loadBestsellerFromJson();
        model.addAttribute("bookList", bestseller);
        model.addAttribute("query", "베스트셀러 TOP20");
        return "books/search";
    }
}