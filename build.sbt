lazy val root = project
  .in(file("."))
  .aggregate(plugin)
  .settings(common: _*)
  .settings(noPublish: _*)
  .settings(
    name := "release-test-root"
  )

lazy val plugin = project
  .in(file("plugin"))
  .settings(common: _*)
  .settings(
    name := "release-test",
    organization := "com.example.test"
  )

// Shared settings

def common = releaseCommonSettings ++ Seq(
  homepage := Some(url("http://example.com")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  bintrayOrganization := Some("jroper"),
  bintrayRepository := "generic",
  bintrayPackage := "release-test",
  bintrayReleaseOnPublish := false,
  aggregate in bintrayRelease := false,
  pomExtra :=
    <scm>
      <url>git@github.com:jroper/release-test.git</url>
      <connection>scm:git:git@github.com:jroper/release-test.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jroper</id>
        <name>James Roper</name>
        <url>https://jazzy.id.au</url>
      </developer>
    </developers>
)


def noPublish = Seq(
  publish := {},
  publishLocal := {},
  PgpKeys.publishSigned := {},
  // publish-signed needs this for some reason...
  publishTo := Some(Resolver.file("Dummy repo", target.value / "dummy-repo"))
)

// Release settings

def releaseCommonSettings: Seq[Setting[_]] = releaseSettings ++ {
  import sbtrelease._
  import ReleaseStateTransformations._
  import ReleaseKeys._

  def promoteBintray = ReleaseStep(
    action = releaseTask(bintrayRelease)
  )

  Seq(
    publishArtifactsAction := PgpKeys.publishSigned.value,
    tagName := (version in ThisBuild).value,

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      promoteBintray,
      pushChanges
    )
  )
}
