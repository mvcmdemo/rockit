app.controller('MachineEditorController', function ($scope, $uibModalInstance, $timeout, machine, machines, platforms, groups, utils) {
    $scope.machine = angular.copy(machine);
    $scope.machines = angular.copy(machines);
    $scope.platforms = angular.copy(platforms);
    $scope.groups = angular.copy(groups);

    $scope.toggleGroup = function(group) {
        var idx = $scope.machine.groups.findIndex(utils.isEqual, group.id);
        // if group exists in machine.groups then we should remove it
        if (idx > -1) {
            $scope.machine.groups.splice(idx, 1);
        }
        // else, add new group
        else {
            $scope.machine.groups.push(group);
        }
    };

    $scope.isGroupChecked = function (group){
        return machine.groups.findIndex(utils.isEqual, group.id) > -1;
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