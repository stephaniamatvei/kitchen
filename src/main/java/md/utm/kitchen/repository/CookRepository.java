package md.utm.kitchen.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import md.utm.kitchen.model.Cook;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CookRepository {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<Cook> findAll() {
        final var file = Paths.get("src/main/resources/cooks.json").toFile();
        return Arrays.asList(objectMapper.readValue(file, Cook[].class));
    }
}
