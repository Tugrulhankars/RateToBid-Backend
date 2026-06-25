package org.racetobid.racetobid.service.impl;

import org.racetobid.racetobid.dto.request.CreateAuctionItemRequest;
import org.racetobid.racetobid.entity.AuctionItem;
import org.racetobid.racetobid.entity.Category;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.repository.AuctionItemRepository;
import org.racetobid.racetobid.repository.AuctionRepository;
import org.racetobid.racetobid.repository.UserRepository;
import org.racetobid.racetobid.service.AuctionItemService;
import org.racetobid.racetobid.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionItemServiceImpl implements AuctionItemService {
    private final AuctionItemRepository auctionItemRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public AuctionItemServiceImpl(AuctionItemRepository auctionItemRepository, CategoryService categoryService, UserRepository userRepository) {
        this.auctionItemRepository = auctionItemRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    @Override
    public String addAuctionItem(CreateAuctionItemRequest request) {
        Optional<Category> category= Optional.ofNullable(categoryService.getCategoryById(request.getCategoryId()));
        if(category.isEmpty()){
            return "Category not found";
        }

        User user=userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        AuctionItem auctionItem=new AuctionItem();
        auctionItem.setName(request.getName());
        auctionItem.setDescription(request.getDescription());
        auctionItem.setStartPrice(request.getStartPrice());
        auctionItem.setCurrentPrice(request.getCurrentPrice());
        auctionItem.setSeller(user);
        auctionItem.setCategory(category.get());
        return "";
    }

    @Override
    public List<AuctionItem> getAllAuctionItemsByCategory(Long categoryId) {

        List<AuctionItem> auctionItems=auctionItemRepository.findAllAuctionItemsByCategoryId(categoryId);

        return auctionItems;
    }

    @Override
    public AuctionItem getAuctionItemDetailsById(Long id) {
        Optional<AuctionItem> auctionItem=auctionItemRepository.findById(id);
        return auctionItem.orElse(null);
    }

    @Override
    public String updateAuctionItem(Long id, CreateAuctionItemRequest request) {
        return "";
    }

    @Override
    public String deleteAuctionItem(Long id) {
        auctionItemRepository.deleteById(id);
        return "";
    }

    @Override
    public List<AuctionItem> getAuctionItemsByAuctionId(Long auctionId) {
        return List.of();
    }
}
