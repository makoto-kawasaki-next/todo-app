/**
  * This is a sample of Todo Application.
  *
  */

package lib.persistence.db

import java.time.LocalDateTime
import slick.jdbc.JdbcProfile
import ixias.persistence.model.{DataSourceName, Table}
import lib.model.TodoCategory

// UserTable: Userテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class TodoCategoryTable[P <: JdbcProfile]()(implicit val driver: P)
  extends Table[TodoCategory, P] {
  import api._

  // Definition of DataSourceName
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  lazy val dsn: Map[String, DataSourceName] = Map(
    "master" -> DataSourceName("ixias.db.mysql://master/todo"),
    "slave"  -> DataSourceName("ixias.db.mysql://slave/todo")
  )

  // Definition of Query
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Query extends BasicQuery(new Table(_)) {}
  lazy val query = new Query

  // Definition of Table
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  class Table(tag: Tag) extends BasicTable(tag, "todo_category") {
    import TodoCategory._
    // Columns
    /* @1 */ def id        = column[Id]            ("id",         O.UInt64, O.PrimaryKey, O.AutoInc)
    /* @2 */ def name = column[String]        ("name",       O.Utf8Char255)
    /* @3 */ def slug       = column[String]         ("slug",        O.Utf8Char64)
    /* @4 */ def color     = column[Int]        ("color",      O.Int16)
    /* @5 */ def updatedAt = column[LocalDateTime] ("updated_at", O.TsCurrent)
    /* @6 */ def createdAt = column[LocalDateTime] ("created_at", O.Ts)

    type TableElementTuple = (
      Option[Id], String, String, Int, LocalDateTime, LocalDateTime
    )

    // DB <=> Scala の相互のmapping定義
    def * = (id.?, name, slug, color, updatedAt, createdAt) <> (
      // Tuple(table) => Model
      (t: TableElementTuple) => new TodoCategory(
        id = t._1, name = t._2, slug = t._3, color = t._4, updatedAt = t._5, createdAt = t._6
      ),
      // Model => Tuple(table)
      (v: TableElementType) => TodoCategory.unapply(v).map { t => (
        t._1, t._2, t._3, t._4, LocalDateTime.now(), t._6
      )}
    )
  }
}