package org.racetobid.racetobid.controller;

/*import org.racetobid.racetobid.entity.AuctionItem;
import org.racetobid.racetobid.repository.AuctionItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class WebController {

    private final AuctionItemRepository auctionItemRepository;

    public WebController(AuctionItemRepository auctionItemRepository) {
        this.auctionItemRepository = auctionItemRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/auctions";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/auctions")
    public String auctions(Model model) {
        List<AuctionItem> items = auctionItemRepository.findAll();
        model.addAttribute("auctions", items);
        return "auctions";
    }

    @GetMapping("/auction/{id}")
    public String auctionDetail(@PathVariable Long id, Model model) {
        AuctionItem item = auctionItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Açık artırma bulunamadı"));
        model.addAttribute("auction", item);
        return "auction-detail";
    }
}*/

