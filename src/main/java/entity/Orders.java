package entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity

public class Orders {

    @Id
    private String id;
    private String date;

    @ManyToOne()
    @JoinColumn(name = "customerId")
    private  Customer customer;

    @OneToMany
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Orders(String id, String date) {
        this.id = id;
        this.date = date;
    }
}
