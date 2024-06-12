package com.poc.batch

import org.springframework.batch.core.ItemReadListener
import org.springframework.stereotype.Component

@Component
class CustomItemReadListener<T : Any> : ItemReadListener<T> {

    override fun afterRead(item: T) {
        println("After reading item: $item")
    }

}