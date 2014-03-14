<!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Message in Session: ${sessionHelloService.helloMessage}</h2>
        <h2>Message from DB: ${databaseHelloService.helloMessage}</h2>
        <h2>Message from EJB(DB): ${ejbHelloService.helloMessage}</h2>
        <form action="hello" method="post">
            <label for="msg">Neue Willkommensnachricht: </label><input type="text" name="msg" id="msg"/>
        </form>
    </body>
</html>
