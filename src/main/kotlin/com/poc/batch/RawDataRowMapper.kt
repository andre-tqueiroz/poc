package com.poc.batch

import com.poc.domain.RawData
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

class RawDataRowMapper : RowMapper<RawData> {

    companion object {
        private const val ID_COLUMN = "id"
        private const val CPF_COLUMN = "cpf"
        private const val NAME_COLUMN = "name"
        private const val AGE_COLUMN = "age"
        private const val SCORE_COLUMN = "score"
        private const val INCOME_COLUMN = "income"
    }

    override fun mapRow(rs: ResultSet, rowNum: Int): RawData? {
        return RawData(
            id = rs.getObject(ID_COLUMN, UUID::class.java),
            cpf = rs.getString(CPF_COLUMN),
            name = rs.getString(NAME_COLUMN),
            age = rs.getInt(AGE_COLUMN),
            score = rs.getInt(SCORE_COLUMN),
            income = rs.getDouble(INCOME_COLUMN)
        )
    }
}