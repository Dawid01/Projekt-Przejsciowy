package pl.mpc.asmo.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mpc.asmo.model.SubscribeType;
import pl.mpc.asmo.model.Subscription;
import pl.mpc.asmo.model.User;
import pl.mpc.asmo.repository.SubscriptionRepository;
import pl.mpc.asmo.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceDb {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public UserServiceDb(UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public SubscribeType getUserSubscription(UUID uuid){
        User user = userRepository.findById(uuid).orElseThrow(()->new EntityNotFoundException("User not found"));
        Subscription sub = user.getSubscription();
        LocalDateTime now = LocalDateTime.now();
        if(sub.getSubscribeEndTime() != null && sub.getSubscribeStartTime() != null){
            ChronoLocalDate subEndDate = sub.getSubscribeEndTime();
            if (ChronoLocalDate.from(now).isBefore(subEndDate)) {
               return sub.getSubscribeType();
            }
        }

        return SubscribeType.FREE;
    }

    public void registerUser(String discordId, String username, String email){

        Optional<User> optionalUser = userRepository.findUserByDiscordId(discordId);
        if(optionalUser.isEmpty()){
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setDiscordId(discordId);
            userRepository.save(newUser);
            System.out.println("NEW USER: " + discordId + " " + username);
            Subscription subscription = new Subscription();
            subscription.setUser(newUser);
            subscription.setSubscribed(false);
            subscription.setSubscribeType(SubscribeType.FREE);
            subscriptionRepository.save(subscription);

        }else {
            User user = optionalUser.get();
            user.setUsername(username);
            user.setEmail(email);
            userRepository.save(user);
            System.out.println("UPDATE USER: " + discordId + " " + username);
        }
    }
}
