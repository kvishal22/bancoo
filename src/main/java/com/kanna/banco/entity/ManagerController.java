package com.kanna.banco.entity;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequiredArgsConstructor
@Hidden
@RequestMapping("api/notanuser")
//@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

        private final UserRepo userRepo;

    @GetMapping("/v1/users") //pagination and sorting
    public ResponseEntity<Page<User>> retriveAllUserswithSearch(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id#desc") String[] sortAndOrder,
            @RequestParam(required = false, defaultValue = "") String searchCriteria){

        final List<Order> orders = Arrays.stream(sortAndOrder).filter(s->s.contains("#")).map(s->s.split("#"))
                .map(arr->new Order(Sort.Direction.fromString(arr[1]),arr[0])).collect(Collectors.toList());

        final Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by(orders));
        final var data = searchCriteria.isBlank() ? userRepo.findAll(pageable)
                :userRepo.findByKeyword(pageable,searchCriteria);
        return ResponseEntity.ok(data);
    }
}
