app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal', 'editableOptions', 'utils',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal, editableOptions, utils) {

        $scope.animationsEnabled = true;

        editableOptions.theme = 'bs3';

        $scope.OSs = ['Windows', 'Unix'];

        $scope.groups = [{id: '1', name: 'SBXA'},{id: '2', name: 'MV'},{id: '3', name: 'CorVU'},{id: '4', name: 'RMob'}];
        $scope.machines = [{
            id: utils.IDgenerator(),
            name: 'eng105',
            host: 'den-vm-eng105.u2lab.rs.com',
            user: 'administrator',
            password: 'U2razzle',
            os: $scope.OSs[0],
            description: 'Windows Server 2015',
            groups: [$scope.groups[1]]
        },
        {
            id: utils.IDgenerator(),
            name: 'lxsb1',
            host: 'den-vm-lxsb1.u2lab.rs.com',
            user: 'upix',
            password: 'U2rivers',
            os: $scope.OSs[1],
            description: 'Linux',
            groups: [$scope.groups[1], $scope.groups[2]]
        }];

        $scope.terminal = function(machine){
            $window.open('/terminal');
            /*$http
                .get('/terminal')
                .then(function(data){
                    //data is link to pdf
                    $window.open(data);
                });*/
        };

        $scope.editMachine = function(machine) {
            openMachineEditor(machine, false);
        };

        $scope.addMachine = function() {
            var machine = {
                id: utils.IDgenerator(),
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


