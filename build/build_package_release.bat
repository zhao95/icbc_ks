SET CLASSPATH=lib\ant.jar;lib\ant-launcher.jar;lib\xercesImpl.jar;lib\JSA.jar;lib\YUIAnt.jar;lib\yuicompressor-2_4_6.jar;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\jre\lib\rt.jar
java -Dant.home=%cd% org.apache.tools.ant.launch.Launcher -f build.xml package_release
echo done
pause