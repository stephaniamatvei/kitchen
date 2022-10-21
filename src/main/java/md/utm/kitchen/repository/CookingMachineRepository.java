package md.utm.kitchen.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import md.utm.kitchen.model.CookingMachine;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CookingMachineRepository {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<CookingMachine> findAll() {
        final var file = Paths.get("src/main/resources/cooking-machines.json").toFile();
        return Arrays.asList(objectMapper.readValue(file, CookingMachine[].class));
    }
}
