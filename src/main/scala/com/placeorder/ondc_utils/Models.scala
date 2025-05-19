package com.placeorder.ondc_utils

import io.getquill.{PostgresZioJdbcContext, SnakeCase}
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



final case class StateModel(id: Int, label: String, code:String, countryCode: String)


object StateModel{
    val  schema = quote {
        querySchema[StateModel]("state")
      }

}



final case class CityModel(label: String, code: String, pincode: String, state_id: Int)


object CityModel{
    val  schema = quote {
        querySchema[CityModel]("city")
      }

}

