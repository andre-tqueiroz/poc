package com.poc.repository

import com.poc.domain.RawData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RawDataRepository : JpaRepository<RawData, UUID>
