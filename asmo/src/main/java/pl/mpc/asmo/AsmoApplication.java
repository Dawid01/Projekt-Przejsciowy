package pl.mpc.asmo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;
@EnableScheduling
@SpringBootApplication
public class AsmoApplication {

	public static void main(String[] args) {

		initializeEnv();
		SpringApplication.run(AsmoApplication.class, args);
	}

	private static void initializeEnv(){
		try {
			Dotenv dotenv = Dotenv.load();
			System.setProperty("ALLOWED_ORIGIN_URL", Objects.requireNonNull(dotenv.get("ALLOWED_ORIGIN_URL")));
			System.setProperty("DB_USER", Objects.requireNonNull(dotenv.get("DB_USER")));
			System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
			System.setProperty("DISCORD_CLIENT_ID", Objects.requireNonNull(dotenv.get("DISCORD_CLIENT_ID")));
			System.setProperty("DISCORD_CLIENT_SECRET", Objects.requireNonNull(dotenv.get("DISCORD_CLIENT_SECRET")));
			System.setProperty("DISCORD_TOKEN", Objects.requireNonNull(dotenv.get("DISCORD_TOKEN")));
			System.setProperty("MCP_BEARER_TOKEN", Objects.requireNonNull(dotenv.get("MCP_BEARER_TOKEN")));
			System.setProperty("AI_ENDPOINT", Objects.requireNonNull(dotenv.get("AI_ENDPOINT")));

		}catch (Exception e){
			throw new RuntimeException("ERROR: Can not load .env", e);
		}

	}
}