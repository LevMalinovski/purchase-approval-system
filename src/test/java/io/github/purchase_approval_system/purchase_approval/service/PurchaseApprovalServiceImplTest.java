package io.github.purchase_approval_system.purchase_approval.service;


import io.github.purchase_approval_system.purchase_approval.config.AppConfiguration;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseApprovalDto;
import io.github.purchase_approval_system.purchase_approval.dto.PurchaseRequestDto;
import io.github.purchase_approval_system.purchase_approval.exception.PurchaseApprovalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class PurchaseApprovalServiceImplTest {
    @Autowired
    private PurchaseApprovalService purchaseApprovalService;

    @Autowired
    private AppConfiguration appConfiguration;

    NotificationService notificationServiceMock = mock(NotificationService.class);
    FinancialCapacityFactorServiceImpl financialCapacityFactorService = mock(FinancialCapacityFactorServiceImpl.class);

    @BeforeEach
    void init() {
        appConfiguration.setMinAmount(60.0);
        appConfiguration.setMaxAmount(1000.0);
        appConfiguration.setMinPeriod(5);
        appConfiguration.setMaxPeriod(10);
        appConfiguration.setStepReduce(100.0);
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountBelowMinimum() {
        // Given
        Double requestedAmount = 50.0; // Below the minimum amount
        int period = 12;

        // When
        // Assert exception is thrown
        PurchaseApprovalException exception = assertThrows(PurchaseApprovalException.class,
                () -> purchaseApprovalService.checkRequest(requestedAmount, period));
        // Then
        assert exception.getMessage().contains("Requested amount is below the allowed minimum");
        assert "MIN_AMOUNT_VIOLATION".equals(exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountExceedsMaximum() {
        // Given
        Double requestedAmount = 20000.0; // Above the maximum amount
        int period = 12;

        // When
        // Assert exception is thrown
        PurchaseApprovalException exception = assertThrows(PurchaseApprovalException.class,
                () -> purchaseApprovalService.checkRequest(requestedAmount, period));
        // Then
        // Verify exception details
        assert exception.getMessage().contains("Requested amount exceeds the allowed maximum");
        assert "MAX_AMOUNT_EXCEEDED".equals(exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenPaymentPeriodBelowMinimum() {
        // Given
        Double requestedAmount = 500.0;
        int period = 3; // Below the minimum period

        // When
        // Assert exception is thrown
        PurchaseApprovalException exception = assertThrows(PurchaseApprovalException.class,
                () -> purchaseApprovalService.checkRequest(requestedAmount, period));

        // Then
        assert exception.getMessage().contains("Payment period is below the allowed minimum");
        assert "MIN_PERIOD_VIOLATION".equals(exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenPaymentPeriodExceedsMaximum() {
        // Given
        Double requestedAmount = 500.0;
        int period = 70; // Above the maximum period

        // When
        // Assert exception is thrown
        PurchaseApprovalException exception = assertThrows(PurchaseApprovalException.class,
                () -> purchaseApprovalService.checkRequest(requestedAmount, period));

        // Then
        assert exception.getMessage().contains("Payment period exceeds the allowed maximum");
        assert "MAX_PERIOD_EXCEEDED".equals(exception.getErrorCode());
    }

    @Test
    void shouldPassWhenRequestIsValid() {
        // Given
        Double requestedAmount = 500.0;
        int period = 8;

        // When Then
        // Assert no exception is thrown
        assertDoesNotThrow(() -> purchaseApprovalService.checkRequest(requestedAmount, period));
    }

    @Test
    void shouldApproveWhenRequestedAmountAndPeriodAreValid() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(50);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 500.0;
        int period = 10;
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);

        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(requestedAmount, purchaseApprovalDto.approvedAmount());
        assertEquals(period, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldApproveWhenRequestedAmountIsAtMinimum() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(50);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 60.0; // Minimum amount
        int period = 8;
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(requestedAmount, purchaseApprovalDto.approvedAmount());
        assertEquals(period, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldApproveWhenRequestedAmountIsAtMaximum() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(200);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);
        Double requestedAmount = 1000.0; // Maximum amount
        int period = 8;
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(requestedAmount, purchaseApprovalDto.approvedAmount());
        assertEquals(period, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldApproveWhenPaymentPeriodIsAtMinimum() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(100);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 500.0;
        int period = 5; // Minimum period
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(requestedAmount, purchaseApprovalDto.approvedAmount());
        assertEquals(period, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldApproveWhenPaymentPeriodIsAtMaximum() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(50);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 500.0;
        int period = 10; // Maximum period
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(requestedAmount, purchaseApprovalDto.approvedAmount());
        assertEquals(period, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldApproveWhenPaymentPeriodIsLessThanAvailable() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(50);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 1000.0;
        int period = 5;
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertTrue(purchaseApprovalDto.approved());
        assertEquals(500.0, purchaseApprovalDto.approvedAmount());
        assertEquals(10, purchaseApprovalDto.approvedPeriod());
    }

    @Test
    void shouldNotApproveWhenFinancialCapacityIsZero() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        when(financialCapacityFactorService.getFactor("123456789")).thenReturn(0);
        PurchaseApprovalServiceImpl service = new PurchaseApprovalServiceImpl(appConfiguration, financialCapacityFactorService, notificationServiceMock);

        Double requestedAmount = 500.0;
        int period = 5; // Minimum period
        PurchaseRequestDto requestDto = new PurchaseRequestDto("123456789", requestedAmount, period);

        // When
        CompletableFuture<PurchaseApprovalDto> future = service.evaluatePurchase(UUID.randomUUID(), requestDto);
        // Then
        PurchaseApprovalDto purchaseApprovalDto = future.get(5, SECONDS);

        assertFalse(purchaseApprovalDto.approved());
        assertEquals(0, purchaseApprovalDto.approvedAmount());
        assertEquals(0, purchaseApprovalDto.approvedPeriod());
    }
}