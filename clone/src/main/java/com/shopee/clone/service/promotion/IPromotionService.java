package com.shopee.clone.service.promotion;

import com.shopee.clone.DTO.promotion.request.PromotionRequestCreate;
import com.shopee.clone.DTO.promotion.response.PromotionResponse;
import com.shopee.clone.DTO.promotion.response.TypeDiscountResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPromotionService {
    ResponseEntity<?> createPromotion(PromotionRequestCreate promotionRequestCreate);
    ResponseEntity<?> editStatusPromotion(Long promotionId, Boolean status);
    Boolean isValidPromotion(String name, Integer purchasedAmount);
    ResponseEntity<?> addPromotionBeLongUser(Long userId, List<Long> listPromotionId);
    //Call To this Service is Enough
    Boolean checkValidUsage(Long userId, String promotionName, Integer purchasedAmount);
    Boolean minusUsage(Long userId, String promotionName, Integer purchasedAmount);
    Boolean plusUsage(Long userId, String promotionName, Integer purchasedAmount);
    ResponseEntity<?> getAllPromotionAvailable();
    ResponseEntity<?> getAllPromotionBySellerId(Long sellerId);
    ResponseEntity<?> getPromotionOfUser(Long userId);
    TypeDiscountResponse getTypeDiscount(String promotionName);
}