package com.server.animalmoa.api.exception

class DataAlreadySavedException(
    message: String = "Data Already Saved",
) : Exception(
        message,
    )
