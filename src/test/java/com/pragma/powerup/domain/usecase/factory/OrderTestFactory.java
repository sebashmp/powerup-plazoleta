package com.pragma.powerup.domain.usecase.factory;

import com.pragma.powerup.domain.model.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.pragma.powerup.domain.usecase.factory.OrderTestConstants.*;

public final class OrderTestFactory {

    private OrderTestFactory() {}

    public static RestaurantModel restaurant() {
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setId(RESTAURANT_ID);
        restaurant.setName("Test Restaurant");
        return restaurant;
    }

    public static RestaurantModel otherRestaurant() {
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setId(OTHER_RESTAURANT_ID);
        restaurant.setName("Other Restaurant");
        return restaurant;
    }

    public static DishModel dishFromRestaurant(RestaurantModel restaurant) {
        DishModel dish = new DishModel();
        dish.setId(DISH_ID);
        dish.setName("Test Dish");
        dish.setRestaurant(restaurant);
        return dish;
    }

    public static DishModel dishFromOtherRestaurant() {
        DishModel dish = new DishModel();
        dish.setId(DISH_ID);
        dish.setRestaurant(otherRestaurant());
        return dish;
    }

    public static OrderModel validOrder() {
        RestaurantModel restaurant = restaurant();
        DishModel dish = dishFromRestaurant(restaurant);

        OrderDishModel orderDish = new OrderDishModel();
        orderDish.setDish(dish);
        orderDish.setQuantity(2);

        OrderModel order = new OrderModel();
        order.setRestaurant(restaurant);
        order.setOrderDishes(Collections.singletonList(orderDish));

        return order;
    }

    public static GenericPage<OrderModel> singleOrderPage() {
        GenericPage<OrderModel> page = new GenericPage<>();
        page.setContent(List.of(validOrder()));
        page.setPageNumber(PAGE);
        page.setPageSize(SIZE);
        page.setTotalElements(1L);
        page.setTotalPages(1);
        page.setFirst(true);
        page.setLast(true);
        page.setHasNext(false);
        page.setHasPrevious(false);
        return page;
    }

    public static GenericPage<OrderModel> emptyOrderPage() {
        GenericPage<OrderModel> page = new GenericPage<>();
        page.setContent(Collections.emptyList());
        page.setPageNumber(PAGE);
        page.setPageSize(SIZE);
        page.setTotalElements(0L);
        page.setTotalPages(0);
        page.setFirst(true);
        page.setLast(true);
        page.setHasNext(false);
        page.setHasPrevious(false);
        return page;
    }
}
