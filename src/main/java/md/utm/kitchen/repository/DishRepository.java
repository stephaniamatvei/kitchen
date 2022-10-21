package md.utm.kitchen.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import md.utm.kitchen.model.Dish;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DishRepository {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<Dish> findByIds(List<Long> ids) {
        final var file = Paths.get("src/main/resources/dishes.json").toFile();
        final var dishes = Arrays.asList(objectMapper.readValue(file, Dish[].class));

        return ids.stream()
                .map((id) -> dishes.stream().filter((i) -> i.getId() == id).findAny().orElseThrow())
                .collect(Collectors.toList());
    }
}
