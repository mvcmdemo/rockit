app.controller('MachineEditorController', function ($scope, $uibModalInstance, $timeout, machine, machines, OSs, groups) {
    $scope.machine = angular.copy(machine);
    $scope.machines = angular.copy(machines);
    $scope.OSs = angular.copy(OSs);
    $scope.groups = angular.copy(groups);

    $scope.toggleGroup = function(group) {
        var idx = $scope.machine.groups.indexOf(group);
        // if group exists in machine.groups then we should remove it
        if (idx > -1) {
            $scope.machine.groups.splice(idx, 1);
        }
        // else, add new group
        else {
            $scope.machine.groups.push(group);
        }
    };

    function validateMachine() {
        return true;
    }

    $scope.save = function() {
        if (!validateMachine()) {
            return;
        }
        $uibModalInstance.close($scope.machine);
    };

    $scope.cancel = function(){
        $uibModalInstance.dismiss('cancel');
    }
});