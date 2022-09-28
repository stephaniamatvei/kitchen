package md.utm.kitchen.service;

import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.service.dto.CookDto;
import md.utm.kitchen.service.dto.CookingMachineDto;
import md.utm.kitchen.service.dto.DishDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static md.utm.kitchen.service.utils.NumberUtils.getRandomNumber;

@Slf4j
@Service
public class OrderDataService {
    private static final List<CookingMachineDto> COOKING_MACHINES = List.of(
            new CookingMachineDto("OVEN"),
            new CookingMachineDto("STOVE")
    );

    private static final List<DishDto> DISHES = List.of(
            new DishDto(1L, "PIZZA", 20, 2, COOKING_MACHINES.get(0)),
            new DishDto(2L, "SALAD", 10, 1, null),
            new DishDto(3L, "ZEAMA", 7, 1, COOKING_MACHINES.get(1)),
            new DishDto(4L, "SCALLOP_SASHIMI_MEYER_LEMON", 32, 3, null),
            new DishDto(5L, "ISLAND_DUCK_MUSTARD", 35, 3, COOKING_MACHINES.get(0)),
            new DishDto(6L, "WAFFLES", 10, 1, COOKING_MACHINES.get(1)),
            new DishDto(7L, "AUBERGINE", 20, 2, COOKING_MACHINES.get(0)),
            new DishDto(8L, "LASAGNA", 30, 2, COOKING_MACHINES.get(0)),
            new DishDto(9L, "BURGER", 15, 1, COOKING_MACHINES.get(1)),
            new DishDto(10L, "GYROS", 15, 1, null),
            new DishDto(11L, "KEBAB", 15, 1, null),
            new DishDto(12L, "UNAGI_MAKI", 20, 2, null),
            new DishDto(13L, "TOBACCO_CHICKEN", 30, 2, COOKING_MACHINES.get(0))
    );

    @Value("${app.totalCooks}")
    private int totalCooks;

    public List<DishDto> findDishesByIds(List<Long> ids) {
        return DISHES.stream().filter((i) -> ids.contains(i.getId())).collect(Collectors.toList());
    }

    public List<CookDto> generateCooks() {
        log.info("Generating cooks...");

        return IntStream.of(1, totalCooks + 1).mapToObj((i) -> {
            final var cook = new CookDto();

            cook.setId((long) i);
            cook.setGivenName("Ion");
            cook.setLastName("Ion");
            cook.setCatchPhrase("Hello!");
            cook.setCookRank(getRandomNumber(1, 3));
            cook.setCookProficiency(3);

            return cook;
        }).collect(Collectors.toList());
    }
}
