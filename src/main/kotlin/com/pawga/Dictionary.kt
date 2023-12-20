package com.pawga

import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Size

/**
 * Created by sivannikov
 */
@Serdeable
data class Dictionary(
    @Size(max = 255) val name: String,
//    val values: List<DictionaryValue>,
)
