app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal', 'editableOptions',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal, editableOptions) {

        $scope.animationsEnabled = true;

        editableOptions.theme = 'bs3';

        $scope.OSs = ['Windows', 'Unix'];

        $scope.groups = [{id: '1', name: 'SBXA'},{id: '2', name: 'MV'},{id: '3', name: 'CorVU'},{id: '4', name: 'RMob'}];
        $scope.machines = [{
            id: '1',
            name: 'eng105',
            hostname: 'den-vm-eng105.u2lab.rs.com',
            username: 'administrator',
            password: 'U2razzle',
            os: $scope.OSs[0],
            description: 'Windows Server 2015',
            groups: [$scope.groups[1].id]
        }];

        $scope.editMachine = function(machine) {
            openMachineEditor(machine, false);
        };

        $scope.addMachine = function() {
            var machine = {
                id: IDgenerator(),
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
                } else {
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

        $scope.addGroup = function () {
            var idx = $scope.groups.findIndex(isEqual, '');
            if (idx === -1) {
                var group = {id: IDgenerator(), name: ''};
                $scope.groups.push(group);
            } else {
                alert('You can\'t add one more group');
            }
        };
        
        function isEqual(element) {
            return element.name === this;
        }

        function IDgenerator() {
            // Math.random should be unique because of its seeding algorithm.
            // Convert it to base 36 (numbers + letters), and grab the first 9 characters
            // after the decimal.
            return '_' + Math.random().toString(36).substr(2, 9);
        }

    }
]);


