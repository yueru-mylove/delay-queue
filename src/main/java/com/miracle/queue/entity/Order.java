package com.miracle.queue.entity;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Order {

    private Integer id;

    private Long orderTime;

}
