package lk.ijse.dep11.customermanagmentsystem.to;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String id;
    private String name;
    private String address;

    private String pictureUrl;
}
