package com.shopee.clone.rest_controller.promotion;

import com.shopee.clone.DTO.fieldErrorDTO.FieldError;
import com.shopee.clone.DTO.promotion.request.PromotionRequestCreate;
import com.shopee.clone.service.promotion.IPromotionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionRestController {
    private final IPromotionService promotionService;

    public PromotionRestController(IPromotionService promotionService) {
        this.promotionService = promotionService;
    }


    @PostMapping("new")
    public ResponseEntity<?> createPromotion(@RequestBody @Valid PromotionRequestCreate promotionRequestCreate,
                                             BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            FieldError.throwErrorHandler(bindingResult);
        }
        return promotionService.createPromotion(promotionRequestCreate);
    }

    @PostMapping("user/{userId}/choose")
    public ResponseEntity<?> addPromotionBeLongUser(@PathVariable Long userId, @RequestBody List<Long> listPromotionId){
        return promotionService.addPromotionBeLongUser(userId, listPromotionId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editStatusPromotion(@PathVariable(name = "id") Long promotionId, @RequestBody Boolean status){
        return promotionService.editStatusPromotion(promotionId,status);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllAvailablePromotion(){
        return promotionService.getAllPromotionAvailable();
    }

    @GetMapping("seller/{id}")
    public ResponseEntity<?> getAllPromotionBySeller(@PathVariable(name = "id") Long sellerId){
        return promotionService.getAllPromotionBySellerId(sellerId);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> getPromotionOfUser(@PathVariable(name = "id") Long userId){
        return promotionService.getPromotionOfUser(userId);
    }
}
