package dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class OrderDto {
    private String id;
    private String date;
    private  String customerId;
    private List<OrderDetailsDto> list;
}
