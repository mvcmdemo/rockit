app.controller('terminalController', function ($scope) {

    // io.on('connection', function(socket){
    //     console.log('a user connected');
    //     socket.on('disconnect', function(){
    //         console.log('user disconnected');
    //     });
    // });
    $scope.runTerminal = function(targetMachineID) {
        Terminal.applyAddon(fit);
        var term = new Terminal();
        term.open(document.getElementById('#terminal'));
        //term.write('Hello from \033[1;3;31mxterm.js\033[0m $ ')
        //term.write('$ ');
        term.focus();
        term.fit();
        term.writeln("Connecting to " + targetMachineID + " ...");
        window.addEventListener('resize', function () {
            term.fit();
        }, false);
        var socket = io('http://localhost:8081/?machine_id=' + targetMachineID);

        term.on('data', function (data) {
            socket.emit('data', data);
            // if (data == '\n') {
            //     term.writeln('');
            // }
            // term.write(data);
        });

        socket.on('data', function (data) {
            term.write(data);
        });
        socket.on('error', function (data) {
            term.writeln('\033[1;3;31m' + data + '\033[0m');
            socket.disconnect();
        })
    };

});