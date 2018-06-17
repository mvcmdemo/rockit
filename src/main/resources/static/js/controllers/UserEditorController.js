app.controller('UserEditorController', function ($scope, $uibModalInstance, $timeout, user, users, groups, utils) {
    $scope.user = angular.copy(user);
    $scope.users = angular.copy(users);
    $scope.groups = angular.copy(groups);

    $scope.toggleGroup = function(group) {
        var idx = $scope.user.groups.findIndex(utils.isEqual, group.id);
        // if group exists in user.groups then we should remove it
        if (idx > -1) {
            $scope.user.groups.splice(idx, 1);
        }
        // else, add new group
        else {
            $scope.user.groups.push(group);
        }
    };

    $scope.isGroupChecked = function (group){
        return user.groups.findIndex(utils.isEqual, group.id) > -1;
    };

    function validateUser() {
        return true;
    }

    $scope.save = function() {
        if (!validateUser()) {
            return;
        }
        $scope.user.enabled = true;
        $uibModalInstance.close($scope.user);
    };

    $scope.cancel = function(){
        $uibModalInstance.dismiss('cancel');
    }
});