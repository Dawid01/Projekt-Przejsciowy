package pl.mpc.asmo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mpc.asmo.context.BotContext;
import pl.mpc.asmo.model.Prompt;
import pl.mpc.asmo.service.BotService;

import java.util.Map;

@RestController
@RequestMapping("/prompt")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    public ResponseEntity<?> sendPrompt(@RequestBody Prompt prompt) {
        BotContext.setGuildId(prompt.discordServerId());

        try {
            String aiResponse = botService.processPrompt(prompt);

            return ResponseEntity.ok(Map.of(
                    "input", prompt.prompt(),
                    "response", aiResponse
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "AI processing failed: " + e.getMessage()));
        } finally {
            BotContext.clear();
        }
    }
}
