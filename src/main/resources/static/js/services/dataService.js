app.service('dataService', [function () {
    var machine;
    return {
        getTargetMachine : function () {
            return machine;
        },
        setTargetMachine : function (value) {
            machine = value;
        }
    }

}]);