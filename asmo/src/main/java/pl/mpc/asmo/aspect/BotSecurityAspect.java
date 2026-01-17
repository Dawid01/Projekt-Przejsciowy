package pl.mpc.asmo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import pl.mpc.asmo.context.BotContext;
import pl.mpc.asmo.exception.BotSecurityException;
import pl.mpc.asmo.service.BotSecurityService;

@Aspect
@Component
public class BotSecurityAspect {

    private final BotSecurityService securityService;

    public BotSecurityAspect(BotSecurityService securityService) {
        this.securityService = securityService;
    }

    @Around("@annotation(pl.mpc.asmo.annotation.RequiresBotAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            String guildId = BotContext.getGuildId();
            String userId = BotContext.getUserId();

            securityService.validateAccess(guildId, userId);

            return joinPoint.proceed();

        } catch (BotSecurityException e) {
            return "SYSTEM INSTRUCTION: The user was denied access. You MUST reply to the user with EXACTLY this message: " + e.getMessage();
        }
    }
}
