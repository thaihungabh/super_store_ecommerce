package com.shopee.clone.repository.order;

import com.shopee.clone.entity.SellerEntity;
import com.shopee.clone.entity.UserEntity;
import com.shopee.clone.entity.order.EOrder;
import com.shopee.clone.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    List<OrderEntity> findAllByUser(UserEntity userEntity);

    List<OrderEntity> findAllBySeller(SellerEntity seller);

    List<OrderEntity> findByConfirmDateAndStatus(Date now, EOrder eOrder);

    List<OrderEntity> findByConfirmDateBetweenAndStatus(Date startOfYesterday, Date endOfYesterday, EOrder eOrder);
}