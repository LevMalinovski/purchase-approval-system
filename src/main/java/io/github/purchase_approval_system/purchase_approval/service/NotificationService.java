package io.github.purchase_approval_system.purchase_approval.service;

import io.github.purchase_approval_system.purchase_approval.dto.PurchaseApprovalDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void sendMessageWithDelay(UUID requestId, PurchaseApprovalDto purchaseApprovalDto, long delayInSeconds) {
        scheduler.schedule(() -> notifyClient(requestId, purchaseApprovalDto),
                delayInSeconds, TimeUnit.SECONDS);
    }

    public void notifyClient(UUID requestId, PurchaseApprovalDto purchaseApprovalDto) {
        messagingTemplate.convertAndSend("/topic/status/" + requestId, purchaseApprovalDto);
    }
}