package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Messages.schema ++ Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Messages
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param streamid Database column streamid SqlType(varchar), Length(50,true)
   *  @param fromUser Database column from_user SqlType(varchar), Length(50,true)
   *  @param userMessage Database column user_message SqlType(varchar), Length(2000,true)
   *  @param messageTime Database column message_time SqlType(varchar), Length(50,true) */
  case class MessagesRow(id: Int, streamid: String, fromUser: String, userMessage: String, messageTime: String)
  /** GetResult implicit for fetching MessagesRow objects using plain SQL queries */
  implicit def GetResultMessagesRow(implicit e0: GR[Int], e1: GR[String]): GR[MessagesRow] = GR{
    prs => import prs._
    MessagesRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table messages. Objects of this class serve as prototypes for rows in queries. */
  class Messages(_tableTag: Tag) extends profile.api.Table[MessagesRow](_tableTag, "messages") {
    def * = (id, streamid, fromUser, userMessage, messageTime) <> (MessagesRow.tupled, MessagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(streamid), Rep.Some(fromUser), Rep.Some(userMessage), Rep.Some(messageTime))).shaped.<>({r=>import r._; _1.map(_=> MessagesRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column streamid SqlType(varchar), Length(50,true) */
    val streamid: Rep[String] = column[String]("streamid", O.Length(50,varying=true))
    /** Database column from_user SqlType(varchar), Length(50,true) */
    val fromUser: Rep[String] = column[String]("from_user", O.Length(50,varying=true))
    /** Database column user_message SqlType(varchar), Length(2000,true) */
    val userMessage: Rep[String] = column[String]("user_message", O.Length(2000,varying=true))
    /** Database column message_time SqlType(varchar), Length(50,true) */
    val messageTime: Rep[String] = column[String]("message_time", O.Length(50,varying=true))
  }
  /** Collection-like TableQuery object for table Messages */
  lazy val Messages = new TableQuery(tag => new Messages(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(50,true)
   *  @param password Database column password SqlType(varchar), Length(100,true) */
  case class UsersRow(id: Int, username: String, password: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (id, username, password) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(username), Rep.Some(password))).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(50,true) */
    val username: Rep[String] = column[String]("username", O.Length(50,varying=true))
    /** Database column password SqlType(varchar), Length(100,true) */
    val password: Rep[String] = column[String]("password", O.Length(100,varying=true))
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
