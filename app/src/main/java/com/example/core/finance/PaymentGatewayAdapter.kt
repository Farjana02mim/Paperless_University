package com.example.core.finance

import com.example.domain.model.finance.PaymentGateway
import com.example.domain.model.finance.PaymentModel
import com.example.domain.model.finance.PaymentStatus
import java.util.UUID

data class PaymentRequest(
    val invoiceId: String,
    val studentId: String,
    val studentName: String,
    val amount: Double,
    val currency: String = "BDT",
    val gateway: PaymentGateway
)

data class PaymentResponse(
    val isSuccess: Boolean,
    val transactionId: String,
    val gatewayRef: String,
    val redirectUrl: String? = null,
    val errorMessage: String? = null
)

interface PaymentGatewayProvider {
    suspend fun initiatePayment(request: PaymentRequest): PaymentResponse
    suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean
}

class BkashGatewayProvider : PaymentGatewayProvider {
    override suspend fun initiatePayment(request: PaymentRequest): PaymentResponse {
        val txId = "BKASH_TX_${UUID.randomUUID().toString().take(8).uppercase()}"
        val ref = "REF_BK_${System.currentTimeMillis() % 100000}"
        return PaymentResponse(
            isSuccess = true,
            transactionId = txId,
            gatewayRef = ref,
            redirectUrl = "https://checkout.bkash.com/payment/$txId"
        )
    }

    override suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean {
        return transactionId.startsWith("BKASH_TX_")
    }
}

class NagadGatewayProvider : PaymentGatewayProvider {
    override suspend fun initiatePayment(request: PaymentRequest): PaymentResponse {
        val txId = "NAGAD_TX_${UUID.randomUUID().toString().take(8).uppercase()}"
        val ref = "REF_NG_${System.currentTimeMillis() % 100000}"
        return PaymentResponse(
            isSuccess = true,
            transactionId = txId,
            gatewayRef = ref,
            redirectUrl = "https://checkout.nagad.com.bd/pay/$txId"
        )
    }

    override suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean {
        return transactionId.startsWith("NAGAD_TX_")
    }
}

class CardGatewayProvider : PaymentGatewayProvider {
    override suspend fun initiatePayment(request: PaymentRequest): PaymentResponse {
        val txId = "CARD_TX_${UUID.randomUUID().toString().take(8).uppercase()}"
        val ref = "REF_SSL_${System.currentTimeMillis() % 100000}"
        return PaymentResponse(
            isSuccess = true,
            transactionId = txId,
            gatewayRef = ref,
            redirectUrl = "https://sslcommerz.com/pay/$txId"
        )
    }

    override suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean {
        return transactionId.startsWith("CARD_TX_")
    }
}

class StripeGatewayProvider : PaymentGatewayProvider {
    override suspend fun initiatePayment(request: PaymentRequest): PaymentResponse {
        val txId = "STRIPE_TX_${UUID.randomUUID().toString().take(8).uppercase()}"
        val ref = "pi_${UUID.randomUUID().toString().take(12)}"
        return PaymentResponse(
            isSuccess = true,
            transactionId = txId,
            gatewayRef = ref,
            redirectUrl = "https://checkout.stripe.com/pay/$ref"
        )
    }

    override suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean {
        return transactionId.startsWith("STRIPE_TX_")
    }
}

class ManualBankProvider : PaymentGatewayProvider {
    override suspend fun initiatePayment(request: PaymentRequest): PaymentResponse {
        val txId = "BANK_SLIP_${UUID.randomUUID().toString().take(8).uppercase()}"
        val ref = "BANK_DEPOSIT_REF_${System.currentTimeMillis() % 10000}"
        return PaymentResponse(
            isSuccess = true,
            transactionId = txId,
            gatewayRef = ref,
            redirectUrl = null
        )
    }

    override suspend fun verifyTransaction(transactionId: String, gatewayRef: String): Boolean {
        return transactionId.startsWith("BANK_SLIP_")
    }
}

class PaymentGatewayFactory {
    companion object {
        fun getProvider(gateway: PaymentGateway): PaymentGatewayProvider {
            return when (gateway) {
                PaymentGateway.BKASH, PaymentGateway.ROCKET -> BkashGatewayProvider()
                PaymentGateway.NAGAD -> NagadGatewayProvider()
                PaymentGateway.VISA_MASTER_CARD, PaymentGateway.AMERICAN_EXPRESS, PaymentGateway.SSLCOMMERZ -> CardGatewayProvider()
                PaymentGateway.STRIPE, PaymentGateway.PAYPAL, PaymentGateway.GOOGLE_PAY, PaymentGateway.APPLE_PAY -> StripeGatewayProvider()
                PaymentGateway.MANUAL_BANK_TRANSFER -> ManualBankProvider()
            }
        }
    }
}
