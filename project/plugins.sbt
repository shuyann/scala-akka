resolvers += Classpaths.typesafeReleases
resolvers += Resolver.bintrayRepo("kamon-io", "sbt-plugins")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")
addSbtPlugin("io.kamon" % "sbt-aspectj-runner" % "1.1.0")
