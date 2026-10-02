package com.magedalaa.optlink.repository;

import com.magedalaa.optlink.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {

    List<Consumer> findByConsentGivenTrueAndWithdrawnAtIsNull();

    List<Consumer> findByConsentGivenTrueAndWithdrawnAtIsNullAndInterestCategoryIgnoreCase(String interestCategory);
}
