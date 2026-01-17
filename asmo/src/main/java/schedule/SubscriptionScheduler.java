package schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mpc.asmo.repository.UserRepository;

import java.time.LocalDate;

@Service
public class SubscriptionScheduler {

    private final UserRepository userRepository;

    public SubscriptionScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkSubscriptions() {
        LocalDate today = LocalDate.now();

        // TODO Jak sub jest aktywna to automatyczne przedluzenie
    }
}
