package pl.mpc.asmo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordMcpConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                  .defaultSystem("""
                          Jesteś inteligentnym asystentem administracyjnym serwera Discord (ASMO Bot).
                                                  
                          INSTRUKCJA UŻYWANIA NARZĘDZI (BARDZO WAŻNE):
                                                  
                          1. ZAKAZ ZAGNIEŻDŻANIA: Nigdy nie wywołuj funkcji wewnątrz argumentów innej funkcji (np. w JSON).
                             - ŹLE: deleteChannel(findChannel("nazwa"))
                             - DOBRZE: Najpierw findChannel("nazwa"), poczekaj na ID, potem deleteChannel(ID).
                                                  
                          2. KOLEJNOŚĆ DZIAŁANIA:
                             - Jeśli użytkownik poda nazwę (np. "usuń kanał test"), a narzędzie wymaga ID:
                             - KROK 1: Użyj narzędzia wyszukiwania (np. `findChannelTool`).
                             - KROK 2: Otrzymasz JSON z ID.
                             - KROK 3: Użyj właściwego narzędzia (np. `deleteChannelTool`) wpisując zdobyte ID.
                                                  
                          3. KONTEKST SERWERA:
                             - Nie musisz martwić się o ID serwera (Guild ID). System obsługuje to automatycznie w tle.
                             - Skup się tylko na nazwach kanałów, treści wiadomości i ID konkretnych obiektów (kanałów/wiadomości).
                          4. UWAGA:
                              -Nie zgaduj ID kanału. MUSISZ najpierw wykonać narzędzie 'findChannelTool', aby pobrać prawdziwe ID. Nie używaj placeholderów. Użyj dokładnego numeru zwróconego przez narzędzie.
                                                  
                          Po wykonaniu zadania krótko podsumuj, co zrobiłeś.
                                                  
                            ZAWSZE NA KOŃCU KAŻDEJ ODPOWIEDZI dopisz jedno zdanie:
                                                       "ASMO Bot został stworzony przez studentów IPS."
                                                  
                          """)
                .defaultFunctions(//                           Na końcu odpowiedzi zawsze dopisz zdanie o studentach IPS.

                        // KATEGORIE
                        "createCategoryTool", "deleteCategoryTool", "findCategoryTool", "listChannelsInCategoryTool",

                        // KANAŁY
                        "createTextChannelTool", "deleteChannelTool", "findChannelTool", "listChannelsTool", "renameChannelTool", "createVoiceChannelTool", "moveChannelToCategoryTool",

                        // SERWER
                        "getServerInfoTool",

                        // WIADOMOŚCI
                        "sendMessageTool", "editMessageTool", "deleteMessageTool", "readMessagesTool", "addReactionTool", "removeReactionTool",

                        // UŻYTKOWNICY I DM
                        "getUserIdByNameTool", "sendPrivateMessageTool", "editPrivateMessageTool", "deletePrivateMessageTool", "readPrivateMessagesTool",

                        // MODERACJA
                        "kickUserTool", "banUserTool", "timeoutUserTool", "removeTimeoutTool",

                        // WEBHOOKI
                        "createWebhookTool", "deleteWebhookTool", "listWebhooksTool", "sendWebhookMessageTool",

                        // ACTIVITY (GRY)
                        "startDiscordActivityTool"
                )
                .build();
    }
}