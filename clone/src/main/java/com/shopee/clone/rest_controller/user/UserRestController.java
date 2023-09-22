package com.shopee.clone.rest_controller.user;

import com.shopee.clone.DTO.auth.user.BecomeSellerRequest;
import com.shopee.clone.DTO.auth.user.ChangePasswordDTO;
import com.shopee.clone.DTO.auth.user.UpdateAddressDTO;
import com.shopee.clone.DTO.auth.user.UserUpdateDTO;
import com.shopee.clone.service.user.UserService;
import com.shopee.clone.validate.RegisterDTOValidate;
import com.shopee.clone.validate.UpdateDTOValidate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UpdateDTOValidate updateDTOValidate;
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Valid
    @RequestBody UserUpdateDTO userUpdateDTO, BindingResult bindingResult) {
        updateDTOValidate.validate(userId,userUpdateDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            // Xử lý lỗi validation
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        return userService.updateUser(userId, userUpdateDTO);
    }
    @PutMapping("/update-address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody UpdateAddressDTO updateAddressDTO) {
        return userService.updateAddress(id, updateAddressDTO);
    }
    @PutMapping("change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO){
        return userService.changePassword(id,changePasswordDTO);
    }
    @GetMapping("/{userName}")
    public ResponseEntity<?> getUserByUserName(@PathVariable String userName){
       return userService.findUserByUserName(userName);
    }

    @PostMapping("/become-seller/{userId}")
    public ResponseEntity<?> becomeSeller(@PathVariable("userId") Long userId, @RequestBody BecomeSellerRequest becomeSellerRequest){
        return null;
    }
}