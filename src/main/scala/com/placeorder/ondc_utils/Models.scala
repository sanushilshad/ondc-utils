package com.placeorder.ondc_utils

import io.getquill.PostgresZioJdbcContext
import io.getquill.SnakeCase
import io.getquill._
final case class CategoryModel(id: String, label: String, code:String, image: String, domainCode: String)


object CategoryModel{
    val  categorySchema = quote {
        querySchema[CategoryModel]("category")
      }

}