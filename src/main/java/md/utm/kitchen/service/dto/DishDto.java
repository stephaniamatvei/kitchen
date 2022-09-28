package md.utm.kitchen.service.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DishDto {
    @EqualsAndHashCode.Include
    private long id;
    private String code;
    private int preparationTime;
    private int requiredCookProficiency;
    private CookingMachineDto cookingMachine;
}
