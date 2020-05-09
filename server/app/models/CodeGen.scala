package models
//Used for generating models only run once. In sbt run 'runMain models.CodeGen' and set the correct app folder path on line 8.
object CodeGen extends App {
  slick.codegen.SourceCodeGenerator.run(
    "slick.jdbc.PostgresProfile", 
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/videostreaming?user=tripphillips1&password=12345678",
    "/Users/tishaphillips1/Desktop/Projects/WebAppsVideoStreaming/server/app", 
    "models", None, None, true, false
  )
}
