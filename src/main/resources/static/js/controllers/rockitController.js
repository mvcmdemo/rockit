app.controller('rockitController', ['$scope', '$q', '$log', '$window', '$timeout', '$http', '$interval', '$rootScope', '$uibModal', 'editableOptions', 'utils', 'dataService', '$window',
    function AdminController($scope, $q, $log, $window, $timeout, $http, $interval, $rootScope, $uibModal, editableOptions, utils, dataService, $window) {


        $(window).resize(function(){
            $('#loginform').css('margin-left',($window.innerWidth - $('#loginform').innerWidth() - 200) + 'px');
            $('#logoutform').css('margin-left',($window.innerWidth - $('#logoutform').innerWidth() - 250) + 'px');
        });
        $(window).resize();

        $scope.animationsEnabled = true;

        $scope.sharedService = dataService;

        editableOptions.theme = 'bs3';

        $scope.users = [];

        $scope.user = {login: 'admin', password: 'admin', admin: true, groups: []};

        $scope.login = function() {
            var username = $('#usernameInput').val();
            var password = $('#passwordInput').val();

            var isFound = false;
            $scope.users.forEach(function(element, index){
                if (element.login === username && element.password === password){
                    $scope.user = $scope.users[index];
                    isFound = true;
                }
            });
            if (!isFound)
                alert('Username or password is incorrect!');
        };

        $scope.logout = function() {
            $scope.user = '';
        };

        $scope.platforms = ['Windows', 'Unix'];

        $scope.groups = [];
        $scope.machines = [];

        $scope.getResources = function() {
            $scope.getAllMachines();
            $scope.getAllGroups();
            $scope.getAllUsers();

            $scope.startMachineMonitoring();
        };

        var monitor_promise;
        // starts the interval
        $scope.startMachineMonitoring = function () {
            // stops any running interval to avoid two intervals running at the same time
            if (angular.isDefined(monitor_promise)) {
                $scope.stopMachineMonitoring();
            }
            // store the interval monitor_promise
            monitor_promise = $interval(updateMachineStatus, 1000);
        };

        // stops the interval
        $scope.stopMachineMonitoring = function () {
            $interval.cancel(monitor_promise);
            monitor_promise = undefined;
        };

        function updateMachineStatus() {
            $http.get('/machine_states').then(function (response) {
                $scope.machines.forEach(function (machine) {
                    machine.state = response.data.states[machine.id];
                    machine.usedBy = response.data.machine_users[machine.id];
                });
            })
        }

        $scope.isMachineAvailForUser = function(machine) {
            if($scope.user.admin) {
                return true;
            }
            var isValid = false;
            if ($scope.user !== '') {
                $scope.user.groups.forEach(function (group) {
                    var idx = machine.groups.findIndex(utils.isEqual, group.id);
                    if (idx > -1) {
                        isValid = true;
                    }
                });
            }
            return isValid;
        };

        $scope.getAllMachines = function() {
            $http.get('/machines').then(function(response){
                $scope.machines = response.data.machines;
            });
        };

        $scope.connectMachine = function(machine) {
            if (machine.platform === 'Windows') {
                $window.open('/rdp/' + machine.id, '_self');
            } else {
                $window.open('/terminal/' + machine.id, '_blank');
            }
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
                platform: 'Windows',
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
                    platforms : function () {
                        return $scope.platforms;
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
                $http.post("/machines", returnMachine).then(
                    function () {
                        if (isNew) {
                            $scope.machines.push(returnMachine);
                            $scope.getAllMachines();
                        } else {
                            var idx = $scope.machines.findIndex(utils.isEqual, id);
                            if(idx > -1) {
                                $scope.machines.splice(idx, 1, returnMachine);
                            }
                        }
                    }, function () {
                        alert("Failed to save '" + returnMachine.name + "' machine");
                    });

            }, function () {
                return false;
            });
        }

        $scope.deleteMachine = function(machine) {
            $http.delete('/machines/' + machine.id).then(function () {
                var idx = $scope.machines.findIndex(utils.isEqual, machine.id);
                $scope.machines.splice(idx, 1);
            });
        };

        $scope.getAllGroups = function() {
            $http.get('/groups').then(function (response) {
                $scope.groups = response.data.groups;
            })
        };

        $scope.addGroup = function () {
            var idx = $scope.groups.findIndex(utils.isEqualName, '');
            if (idx === -1) {
                var group = {id: utils.IDgenerator(), name: ''};
                $scope.groups.push(group);
            } else {
                alert('You can\'t add one more group');
            }
        };

        $scope.acceptGroup = function(groupName, index){
            var idx = $scope.groups.findIndex(utils.isEqualName, groupName);
            // if we find group with the same name and it is not the same group return an error
            if (idx > -1 && index !== idx) {
                return "The group with the same name has already existed";
            }
            // if it's not the same group
            if (idx !== index) {
                var group = {id: 0, name: groupName};
                $http.post('/groups', group).then(function () {
                    $scope.getAllGroups();
                });

            }
        };

        $scope.deleteGroup = function(groupID) {
            $http.delete('/groups/' + groupID).then(function () {
                var idx = $scope.groups.findIndex(utils.isEqual, groupID);
                $scope.groups.splice(idx, 1);
            }, function (reason) {
                alert('Failed to remove the group: ' + reason)
            });
        };

        $scope.getAllUsers = function () {
            $http.get('/users').then(function (response) {
                $scope.users = response.data.users;
            })
        };

        $scope.editUser = function(user) {
            openUserEditor(user, false);
        };

        $scope.addUser = function() {
            var user = {
                id: 0,
                login: '',
                password: '',
                name: '',
                email: '',
                admin: '',
                groups: []
            };
            openUserEditor(user, true);
        };

        function openUserEditor(user, isNew) {
            var id = user.id;
            var modalInstance = $uibModal.open({
                backdrop : false,
                animation : $scope.animationsEnabled,
                templateUrl : 'views/user.html',
                controller : 'UserEditorController',
                size : 'lg',
                resolve : {
                    user : function () {
                        return user;
                    },
                    users : function () {
                        return $scope.users;
                    },
                    groups : function () {
                        return $scope.groups;
                    },
                    utils : function () {
                        return utils;
                    }
                }
            });
            modalInstance.result.then(function (returnUser) {
                $http.post("/users", returnUser).then(
                    function () {
                        if (isNew) {
                            $scope.users.push(returnUser);
                            $scope.getAllUsers();
                        } else {
                            var idx = $scope.users.findIndex(utils.isEqual, id);
                            if(idx > -1) {
                                $scope.users.splice(idx, 1, returnUser);
                            }
                        }
                    }, function () {
                        alert("Failed to save '" + returnUser.name + "' user");
                    });

            }, function () {
                return false;
            });
        }

        $scope.sendMessage = function(_user) {
            var modalInstance = $uibModal.open({
                backdrop : false,
                animation : $scope.animationsEnabled,
                templateUrl : 'views/sendMessage.html',
                controller : 'SendMessageController',
                size : 'lg',
                resolve : {
                    user : function () {
                        return _user;
                    }
                }
            });
            modalInstance.result.then(function (returnUser) {}

            , function () {
                return false;
            });

        };

        $scope.sortTable = function (tableId, n) {
            var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
            table = document.getElementById(tableId);
            switching = true;
            // Set the sorting direction to ascending:
            dir = "asc";
            /* Make a loop that will continue until
            no switching has been done: */
            while (switching) {
                // Start by saying: no switching is done:
                switching = false;
                rows = table.getElementsByTagName("TR");
                /* Loop through all table rows (except the
                first, which contains table headers): */
                for (i = 1; i < (rows.length - 1); i++) {
                    // Start by saying there should be no switching:
                    shouldSwitch = false;
                    /* Get the two elements you want to compare,
                    one from current row and one from the next: */
                    x = rows[i].getElementsByTagName("TD")[n];
                    y = rows[i + 1].getElementsByTagName("TD")[n];
                    /* Check if the two rows should switch place,
                    based on the direction, asc or desc: */
                    if (dir == "asc") {
                        if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                            // If so, mark as a switch and break the loop:
                            shouldSwitch = true;
                            break;
                        }
                    } else if (dir == "desc") {
                        if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                          // If so, mark as a switch and break the loop:
                          shouldSwitch = true;
                          break;
                        }
                    }
                }
                if (shouldSwitch) {
                  /* If a switch has been marked, make the switch
                  and mark that a switch has been done: */
                  rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
                  switching = true;
                  // Each time a switch is done, increase this count by 1:
                  switchcount ++;
                } else {
                  /* If no switching has been done AND the direction is "asc",
                  set the direction to "desc" and run the while loop again. */
                  if (switchcount == 0 && dir == "asc") {
                    dir = "desc";
                    switching = true;
                  }
                }
            }
        }

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


