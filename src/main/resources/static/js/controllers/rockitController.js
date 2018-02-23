app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal) {

        $scope.animationsEnabled = true;

        $scope.OSs = ['Windows', 'Unix'];

        $scope.machines = [];
        $scope.groups = ['SBXA', 'MV', 'CorVU', 'RMob'];

        $scope.addMachine = function() {
            var machine = {
                name: '',
                hostname: '',
                username: '',
                password: '',
                os: '',
                description: '',
                groups: []
            };
            openMachineEditor(machine, true);
        };

        function openMachineEditor(machine, isNew) {
            var oldMachineName = machine.name;
            var modalInstance = $uibModal.open({
                backdrop : false,
                animation : $scope.animationsEnabled,
                templateUrl : 'views/machine.html',
                controller : 'MachineEditorController',
                size : 'lg',
                resolve : {
                    machine : function () {
                        return machine;
                    },
                    machines : function () {
                        return $scope.machines;
                    },
                    OSs : function () {
                        return $scope.OSs;
                    },
                    groups : function () {
                        return $scope.groups;
                    }

                }
            });
            modalInstance.result.then(function (returnMachine) {
                if (isNew) {
                    // add a new machine

                }
                else{
                    // edit an existing machine

                }
            }, function () {
                return false;
            });
        }

    }
]);


