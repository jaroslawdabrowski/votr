Nykredit REST Archetype Service
===============================

The Nykredit REST archetype service shows how to structure and implement a simple REST based service in Nykredit.

The archetype consists of a simple resource - "account" - which is assembled into one deployable war file. For real
use the account resource should be replaced with one or more resources as appropriate to the service actually
being implemented.

Note that the accounts resource is only meant as an example resource setting up the application infrastructure to
build a REST service. The modelling of account is purely fictional and a real life production service would include
things as paging of large result sets, correct caching headers, etc.

The exception mappers and part of the application initialization in the ServicesApplication class is candidates for
going into the NIC framework as a general solution.


Useful Commands
---------------

To build the whole project:

    mvn package

To build project including running integration tests:

    mvn verify

To run in Wildfly:

    mvn -N -Pwildfly cargo:run

To run in WebLogic:

    mvn -N -Pweblogic cargo:run

To redeploy in Wildfly

    mvn -N -Pwildfly cargo:redeploy

To redeploy in WebLogic

    mvn -N -Pweblogic cargo:redeploy

Admin console
-------------
The WebLogic console may be accessed from http://localhost:7001/console using username "weblogic" and password "weblogic1".
The JBoss console may be accessed from http://localhost:9990 using username "advisor1" and password "passw0rd".
