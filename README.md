Zielplatform
============
Getestest auf Wildfly 8.0.0.

Arquilllian-Tests
=================
Die Arquillian-Tests werden automatisch während des Maven-Builds durchgeführt. Dafür wird der aktuelle 
Wildfly aus dem Maven-Repository geladen, entpackt und gestartet. Soll stattdessen eine lokale Wildfly-
Installation verwendet werden, kann das in der Datei `arquillian.xml` konfiguriert werden.
