package com.poc.batch

import org.springframework.batch.core.ChunkListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.stereotype.Component


@Component
class CustomChunkListener : ChunkListener {

    override fun beforeChunk(chunkContext: ChunkContext) {
        //println("Before chunk processing: " + chunkContext.stepContext)
    }

    override fun afterChunk(chunkContext: ChunkContext) {
        println("After chunk processing: " + chunkContext.stepContext.stepExecution.commitCount)
    }

    override fun afterChunkError(chunkContext: ChunkContext) {
        System.err.println("Error during chunk processing: " + chunkContext.stepContext)
    }

}
