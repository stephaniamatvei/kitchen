package md.utm.kitchen.service.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CookDto {
    @EqualsAndHashCode.Include
    private Long id;
    private String givenName;
    private String lastName;
    private String catchPhrase;
    private int cookRank;
    private int cookProficiency;

    private final Lock lock = new ReentrantLock();
}
