package pl.mpc.asmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.mpc.asmo.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {


    @Query("SELECT u FROM User u WHERE u.discordId = :discordId")
    Optional<User> findUserByDiscordId(@Param("discordId") String discordId);
}
