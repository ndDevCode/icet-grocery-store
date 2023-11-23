package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class PlaceOrderTm extends RecursiveTreeObject<PlaceOrderTm> {
    private String code;
    private String description;
    private int qty;
    private double unitPrice;
    private double amount;
    private JFXButton btnDelete;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceOrderTm orderTm = (PlaceOrderTm) o;
        return Objects.equals(code, orderTm.code);
    }
}
