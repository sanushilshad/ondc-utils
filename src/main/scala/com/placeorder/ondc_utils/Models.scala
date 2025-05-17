package com.placeorder.ondc_utils

import io.getquill.PostgresZioJdbcContext
import io.getquill.SnakeCase
import io.getquill._
final case class CategoryModel(id: Int, label: String, code:String, image: String, domain_id: Int)


object CategoryModel{
    val  schema = quote {
        querySchema[CategoryModel]("category")
      }

}


final case class DomainModel(id: Int, label: String, code:String, image: String)


object DomainModel{
    val  schema = quote {
        querySchema[DomainModel]("domain")
      }

}
