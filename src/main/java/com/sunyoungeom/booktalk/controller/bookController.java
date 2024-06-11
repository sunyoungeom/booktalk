package com.sunyoungeom.booktalk.controller;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
@Slf4j
public class bookController {

//    @GetMapping("/search")
//    public String search(Model model) {
//        // 베스트 셀러 리스트
//        model.addAttribute("title", "미움받을 용기");
//        return "books/search";
//    }

//    @RequestMapping("/search/{keyword}")
//    public String searchBooks(@PathVariable String keyword, Model model) {
//        model.addAttribute("keyword", keyword);
//        System.out.println(keyword);
//        return "books/search";
//    }

    @GetMapping("/detail/{title}")
    public String detail(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        return "books/detail";
    }

    @GetMapping("/search")
    public String getBooksSearch(Model model) {
        try {
            List<Map<String, String>> elementList = fetchBestSellers();
            model.addAttribute("bookList", elementList);
            model.addAttribute("query", "베스트셀러 TOP20");
            return "books/search";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }


    private List<Map<String, String>> fetchBestSellers() throws IOException {
//        List<String> elementList = new ArrayList<>();
        List<Map<String, String>> elementList = new ArrayList<>();
        String url = "https://www.yes24.com/Product/Category/BestSeller?categoryNumber=001&pageNumber=1&pageSize=24";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".img_bdr .lazy");
        Elements elements1 = doc.select("span.authPub.info_auth a[href]");
        Elements elements2 = doc.select("span.authPub.info_date");

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            Element element1 = elements1.get(i);
            Element element2 = elements2.get(i);

            String title = element.attr("alt");
            String imgPath = element.attr("data-original");
            String author = element1.text();
            String date = element2.text().replace("년 ", "-").replace("월", "");


            Map<String, String> elementMap = new HashMap<>();
            elementMap.put("title", title);
            elementMap.put("imgPath", imgPath);
            elementMap.put("author", author);
            elementMap.put("date", date);

            // 리스트에 맵 추가
            elementList.add(elementMap);
        }
        log.info(elementList.toString());
        return elementList;
    }
}