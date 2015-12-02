@ECHO OFF
set CLASSPATH=dist/patrimonio-finanzas-1.0.0.jar
set CLASSPATH=%CLASSPATH%;dist\lib\commons-codec-1.3.jar;dist\lib\commons-logging-1.1.1.jar;dist\lib\dom4j-1.6.1.jar;dist\lib\geronimo-stax-api_1.0_spec-1.0.jar;dist\lib\itextpdf-5.1.3.jar;dist\lib\jtds-1.2.4.jar;dist\lib\log4j-1.2.16.jar;dist\lib\ooxml-schemas-1.1.jar;dist\lib\ooxml-security-1.0.jar;dist\lib\poi-3.6.jar;dist\lib\poi-ooxml-3.6.jar;dist\lib\poi-ooxml-schemas-3.6.jar;dist\lib\stax-api-1.0.1.jar;dist\lib\xml-apis-1.3.04.jar;dist\lib\xmlbeans-2.3.0.jar;
                                
echo %CLASSPATH%;

"%JAVA_HOME%"\bin\java -Xms1024m -Xmx1024m -Xnoclassgc es.vdiaz.patrimionio.finanzas.exec.YahooFinance C:\Users\vdiaz\Dropbox\Patrimonio\Finanzas\cotizaciones\

