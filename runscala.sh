time java -Xmx2048M -Xss1m -cp .:/usr/local/Cellar/scala/2.10.0/libexec/lib/scala-library.jar -Dpar=1 main.scala.Hogwild.BigDataProcessor

# Windows
java -Xmx4000M -cp "C:\Program Files (x86)\scala\lib\scala-library.jar";. -Dpar=6 main.scala.Hogwild.BigDataProcessor 5
