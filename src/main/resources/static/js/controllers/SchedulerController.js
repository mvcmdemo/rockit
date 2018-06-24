app.controller('SchedulerController', function ($uibModalInstance, $scope, machine) {
    $scope.machine = angular.copy(machine);


    $scope.cancel = function(){
        $uibModalInstance.dismiss('cancel');
    }
});