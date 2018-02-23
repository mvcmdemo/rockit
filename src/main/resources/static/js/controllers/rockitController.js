app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal) {

        $scope.animationsEnabled = true;

        $scope.OSs = ['Windows', 'Unix'];

        $scope.groups = ['SBXA', 'MV', 'CorVU', 'RMob'];
        $scope.machines = [{
            name: 'eng105',
            hostname: 'den-vm-eng105.u2lab.rs.com',
            username: 'administrator',
            password: 'U2razzle',
            os: $scope.OSs[0],
            description: 'Windows Server 2015',
            groups: [$scope.groups[1]]
        }];

        $scope.editMachine = function(machine) {
            openMachineEditor(machine, false);
        };

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
                    $scope.machines.push(returnMachine);
                }
                else{
                    // edit an existing machine
                    var idx = $scope.machines.findIndex(isEqual, oldMachineName);
                    if(idx > -1) {
                        $scope.machines.splice(idx, 1);
                        $scope.machines.push(returnMachine);
                    }

                }
            }, function () {
                return false;
            });
        }
        
        function isEqual(element) {
            return element.name == this;
        }

    }
]);


