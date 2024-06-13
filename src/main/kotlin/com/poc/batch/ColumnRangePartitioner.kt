package com.poc.batch

import org.springframework.batch.core.partition.support.Partitioner
import org.springframework.batch.item.ExecutionContext
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class ColumnRangePartitioner: Partitioner {

    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var table: String
    private lateinit var column: String

    fun setTable(table: String) {
        this.table = table
    }

    fun setColumn(column: String) {
        this.column = column
    }

    fun setDataSource(dataSource: DataSource) {
        jdbcTemplate = JdbcTemplate(dataSource)
    }

    override fun partition(gridSize: Int): MutableMap<String, ExecutionContext> {
        val min = jdbcTemplate.queryForObject("SELECT MIN(rd.$column) from $table rd left join person p on rd.id = p.id where p.id is null", Int::class.java)!!
        val max = jdbcTemplate.queryForObject("SELECT MAX(rd.$column) from $table rd left join person p on rd.id = p.id where p.id is null", Int::class.java)!!
        val targetSize = (max - min) / gridSize + 1

        val result: MutableMap<String, ExecutionContext> = HashMap()
        var number = 0
        var start = min
        var end = start + targetSize - 1

        while (start <= max) {
            val value = ExecutionContext()
            result.put("partition$number", value)

            if (end >= max) {
                end = max
            }
            value.putInt("minValue", start)
            value.putInt("maxValue", end)
            start += targetSize
            end += targetSize
            number++
        }

        return result
    }
}