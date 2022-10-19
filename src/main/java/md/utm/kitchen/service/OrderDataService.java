package md.utm.kitchen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import md.utm.kitchen.service.dto.CookDto;
import md.utm.kitchen.service.dto.CookingMachineDto;
import md.utm.kitchen.service.dto.DishDto;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderDataService {

    private final ObjectMapper objectMapper;
    private final List<DishDto> dishes;

    @SneakyThrows
    public OrderDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        dishes = Arrays.asList(objectMapper.readValue(Paths.get("src/main/resources/dishes.json").toFile(), DishDto[].class));
    }

    public List<DishDto> findDishesByIds(List<Long> ids) {
        return dishes.stream().filter((i) -> ids.contains(i.getId())).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<CookDto> readCooks() {
        log.info("Reading cooks...");
        return Arrays.asList(objectMapper.readValue(Paths.get("src/main/resources/cooks.json").toFile(), CookDto[].class));
    }

    @SneakyThrows
    public List<CookingMachineDto> readCookingMachines() {
        log.info("Reading cooking machines...");
        return Arrays.asList(objectMapper.readValue(Paths.get("src/main/resources/cooking-machines.json").toFile(), CookingMachineDto[].class));
    }
}
