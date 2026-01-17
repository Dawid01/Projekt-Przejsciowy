package pl.mpc.asmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;
    @Enumerated(EnumType.STRING)
    private SubscribeType subscribeType;
    private LocalDate subscribeStartTime;
    private LocalDate subscribeEndTime;
    private boolean isSubscribed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscribeType getSubscribeType() {
        return subscribeType;
    }

    public void setSubscribeType(SubscribeType subscribeType) {
        this.subscribeType = subscribeType;
    }

    public LocalDate getSubscribeStartTime() {
        return subscribeStartTime;
    }

    public void setSubscribeStartTime(LocalDate subscribeStartTime) {
        this.subscribeStartTime = subscribeStartTime;
    }

    public LocalDate getSubscribeEndTime() {
        return subscribeEndTime;
    }

    public void setSubscribeEndTime(LocalDate subscribeEndTime) {
        this.subscribeEndTime = subscribeEndTime;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }
}
