app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal', 'editableOptions', 'utils', 'dataService', '$window',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal, editableOptions, utils, dataService, $window) {

        $scope.animationsEnabled = true;

        $scope.sharedService = dataService;

        editableOptions.theme = 'bs3';

        $scope.OSs = ['Windows', 'Unix'];

        $scope.groups = [];
        $scope.machines = [];

        $scope.getResources = function() {
            $scope.getAllMachines();
        };

        $scope.getAllMachines = function() {
            $http.get('/machines').then(function(response){
                $scope.machines = response.data.machines;
            });
        };

        $scope.terminal = function(machine) {
            $window.open('/terminal/' + machine.id, '_blank');
        };

        $scope.deleteMachine = function(machine) {
            $http.delete('/machine/' + machine.id).then(function () {
                var idx = $scope.machines.findIndex(utils.isEqual, machine.id);
                $scope.machines.splice(idx, 1);
            });
        };

        $scope.editMachine = function(machine) {
            openMachineEditor(machine, false);
        };

        $scope.addMachine = function() {
            var machine = {
                id: 0,
                name: '',
                host: '',
                user: '',
                password: '',
                os: '',
                description: '',
                groups: []
            };
            openMachineEditor(machine, true);
        };

        function openMachineEditor(machine, isNew) {
            var id = machine.id;
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
                    },
                    utils : function () {
                        return utils;
                    }

                }
            });
            modalInstance.result.then(function (returnMachine) {
                if (isNew) {
                    // add a new machine
                    $http.post("/machines", returnMachine).then(
                        function () {
                            $scope.machines.push(returnMachine);
                        }, function () {
                            alert("Failed to save '" + returnMachine.name + "' machine");
                    });

                } else {
                    // edit an existing machine
                    var idx = $scope.machines.findIndex(utils.isEqual, id);
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
            var idx = $scope.groups.findIndex(utils.isEqualName, '');
            if (idx === -1) {
                var group = {id: utils.IDgenerator(), name: ''};
                $scope.groups.push(group);
            } else {
                alert('You can\'t add one more group');
            }
        };

        $rootScope.$on('http.error', function (event, status) {
            switch (status) {
//				case  - 1:
//					// showError($scope.messages["serviceIsUnreachable"]);
//					break;
                case 500:
                    // showError($scope.messages["serviceIsUnreachable"]);
                    return;
                case 400:
                    // showError($scope.messages["serviceIsUnreachable"]);
                    return;
                default:
                    // showError($scope.messages["serviceIsUnreachable"]);
                    //destroy();
                    //$scope.loadingUser = false;
                    break;
            }
        });

    }
]);


