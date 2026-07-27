package com.spring.cardmarketplace.repositories;

import com.spring.cardmarketplace.entities.Listing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ListingSpecifications {
    public static Specification<Listing> hasCardName(String cardName){
        return (root, query, cb) -> cb.equal(root.join("card").get("cardName"), cardName);
    }

    public static Specification<Listing> hasSetName(String setName){
        return (root, query, cb) -> cb.equal(root.join("card").get("setName"), setName);
    }

    public static Specification<Listing> hasCondition(String condition){
        return (root, query, cb) -> cb.equal(root.get("condition"), condition);
    }

    public static Specification<Listing> hasPrinting(String printing){
        return (root, query, cb) -> cb.equal(root.get("printing"), printing);
    }

    public static Specification<Listing> priceBetween(BigDecimal min, BigDecimal max){
        return (root, query, cb) -> cb.between(root.get("askingPrice"), min, max);
    }

    public static Specification<Listing> priceBelow(BigDecimal max){
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("askingPrice"), max);
    }

    public static Specification<Listing> priceAbove(BigDecimal min){
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("askingPrice"), min);
    }

    public static Specification<Listing> isActive(){
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }


    public static Specification<Listing> withCardAndSeller() {
        return ((root, query, criteriaBuilder) -> {
            if(Long.class != query.getResultType()) {
                root.fetch("card");
                root.fetch("seller");
            }
            return null;
        });
    }
}
