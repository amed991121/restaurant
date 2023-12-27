package com.savent.restaurant.domain.usecase

class ShareReceiptUseCase(
    val getReceiptToPrintUseCase: GetReceiptToPrintUseCase,
    val getReceiptToSendUseCase: GetReceiptToSendUseCase
)