package md.utm.kitchen.service.dto;

import lombok.*;

import java.util.concurrent.locks.ReentrantLock;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CookingMachineDto {
    private final Long id;

    @EqualsAndHashCode.Include
    private final String apparatusCode;

    private final ReentrantLock lock = new ReentrantLock();
}
