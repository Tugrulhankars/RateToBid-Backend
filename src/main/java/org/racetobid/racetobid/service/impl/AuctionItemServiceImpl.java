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
    private final org.racetobid.racetobid.repository.AuctionRepository auctionRepository;

    public AuctionItemServiceImpl(AuctionItemRepository auctionItemRepository, CategoryService categoryService, UserRepository userRepository, org.racetobid.racetobid.repository.AuctionRepository auctionRepository) {
        this.auctionItemRepository = auctionItemRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public String addAuctionItem(CreateAuctionItemRequest request) {
        Optional<Category> category= Optional.ofNullable(categoryService.getCategoryById(request.getCategoryId()));
        if(category.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }

        User user = null;
        if (request.getSellerId() != null) {
            user = userRepository.findById(request.getSellerId()).orElse(null);
        }
        if (user == null) {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                user = userRepository.findByEmail(auth.getName()).orElse(null);
            }
        }
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }


        AuctionItem auctionItem=new AuctionItem();
        auctionItem.setName(request.getName());
        auctionItem.setDescription(request.getDescription());
        auctionItem.setStartPrice(request.getStartPrice());
        auctionItem.setCurrentPrice(request.getCurrentPrice());
        auctionItem.setSeller(user);
        auctionItem.setCategory(category.get());
        
        auctionItemRepository.save(auctionItem);
        return String.valueOf(auctionItem.getId());
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
        Optional<org.racetobid.racetobid.entity.Auction> auction = auctionRepository.findById(auctionId);
        if (auction.isPresent() && auction.get().getAuctionItem() != null) {
            return List.of(auction.get().getAuctionItem());
        }
        return List.of();
    }
}
