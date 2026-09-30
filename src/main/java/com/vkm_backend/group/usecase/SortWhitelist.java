package com.vkm_backend.group.usecase;

import com.vkm_backend.infra.global.exceptions.IllegalFieldArgumentException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SortWhitelist {

    private SortWhitelist() {
    }


    public static Pageable validate(Pageable pageable, Map<String, String> allowedFields){

        List<Sort.Order> orders = new ArrayList<>();

        for (Sort.Order order: pageable.getSort()){
            String property = allowedFields.get(order.getProperty());

            if (property == null){
                throw new IllegalFieldArgumentException("Campo de ordenação não permitido: "
                        + order.getProperty());
            };

            orders.add(new Sort.Order(order.getDirection(), property));
        }

        Sort sort = Sort.by(orders);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
