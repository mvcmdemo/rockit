app.controller('terminalController', function ($scope) {

    // io.on('connection', function(socket){
    //     console.log('a user connected');
    //     socket.on('disconnect', function(){
    //         console.log('user disconnected');
    //     });
    // });
    $scope.targetMachineID = $('#targetMachineID').val();
    $scope.targetMachineHost = $('#targetMachineHost').val();

    $scope.runTerminal = function() {
        Terminal.applyAddon(fit);
        var term = new Terminal();
        term.open(document.getElementById('#terminal'));
        //term.write('Hello from \033[1;3;31mxterm.js\033[0m $ ')
        //term.write('$ ');
        term.focus();
        term.fit();
        term.writeln("Connecting to " + $scope.targetMachineHost + " ...");
        document.title = $scope.targetMachineHost;
        var socket = io('http://ams-vm-kk01.rocketsoftware.com:8081/?machine_id=' + $scope.targetMachineID);

        window.addEventListener('resize', function () {
            term.fit();
            socket.emit('geometry', {cols : term.cols - 1, rows : term.rows - 1})
        }, false);

        socket.on('connect', function () {
            socket.emit('geometry', {cols : term.cols -1 , rows : term.rows - 1})
        });

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