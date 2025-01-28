package com.server.animalmoa.exception

class DataAlreadySavedException(
    message: String = "Data Already Saved",
) : Exception(
        message,
    )
